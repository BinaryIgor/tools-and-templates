from commons import meta, crypto
from datetime import datetime
from os import path
import shutil

CI_PACKAGE_TARGET = "CI_PACKAGE_TARGET"

SECRETS_BUILD_ENV_PREFIX = "secrets:"
BUILD_ENV = "build_env"
LOAD_AND_RUN_SCRIPT = "load_and_run.bash"

log = meta.new_log("build_app")

args = meta.cmd_args({
    "app": {
        "help": "Name of the app",
        "required": True
    },
    "skip_image_export": {
        "help": "Don't export final image to tar, which is time-consuming and not needed for local builds (in most cases)",
        "action": "store_true"
    }
}, script_description="Script to build given app")


def new_tag(app_name):
    if meta.is_local_env() or ("grafana" in app_name):
        return "latest"

    return datetime.utcnow().strftime("%Y%m%d%H%M%S")


def build_app(app, app_name, tag):
    app_dir = path.join(meta.root_dir(), meta.app_dir(app))

    log.info(f"About to build {app_name} app docker image ({tag}) from path: {app_dir}")

    app_config = meta.app_config(app)
    app_build_cmd = build_cmd(app_config)
    if app_build_cmd:
        log.info(f"Executing build cmd with app env: {app_build_cmd}")

        meta.execute_bash_script(f"""
            cd {app_dir}
            {app_build_env_exports_str(app_config, app_name)}
            {app_build_cmd}
        """)
        log.info("build cmd executed")

    meta.execute_bash_script(f"""
    cd {app_dir}    
    docker build . -t {app_name}:{tag}""")


def app_build_env_exports_str(app_config, app_name):
    env = app_config.get(BUILD_ENV, {})
    exports = [f'export {CI_PACKAGE_TARGET}={meta.cli_app_package_dir(app_name)}']

    for k, v in env.items():
        v_str = str(v)
        if v_str.startswith(SECRETS_BUILD_ENV_PREFIX):
            secret = v_str.replace(SECRETS_BUILD_ENV_PREFIX, "")
            value = crypto.system_secrets()[secret]
        else:
            value = v

        exports.append(f'export {k}="{value}"')

    return "\n".join(exports)


def package_app(app, app_name, tag, skip_image_export=False):
    log.info(f"About to create a package for {app_name} app...")

    log.info("Getting app config...")
    app_config = meta.app_config(app)
    log.info("App config loaded")

    tagged_image_name = f'{app_name}:{tag}'

    app_package_dir = meta.cli_app_package_dir(app_name)
    docker_image_tar = f'{app_name}.tar.gz'
    docker_image_path = path.join(app_package_dir, docker_image_tar)

    if skip_image_export:
        log.info("Skipping image export!")
    else:
        log.info(f"Dirs created. Exporting docker image to {docker_image_path}, this can take a while...")
        meta.execute_bash_script(f"docker save {tagged_image_name} | gzip > {docker_image_path}")

    with open(path.join(app_package_dir, "run.bash"), "w") as f:
        run_script = prepared_run_script(app_name, app_config, tagged_image_name)
        f.write(run_script)

    with open(path.join(app_package_dir, LOAD_AND_RUN_SCRIPT), "w") as f:
        load_and_run_script = prepared_load_and_run_script(tagged_image_name, docker_image_tar)
        f.write(load_and_run_script)


def prepared_run_script(app_name, app_config, tagged_image_name):
    zero_downtime_deploy = zero_downtime_deploy_config(app_config)
    run_script_template_path = run_template(zero_downtime_deploy)

    restart_policy = "" if meta.is_local_env() else "--restart unless-stopped"
    stop_timeout = meta.env_config().get('apps-stop-timeout', "30")

    s_comments = script_comments(app_config)
    if s_comments:
        comment = "\n".join([f"#{c}" for c in s_comments])
    else:
        comment = ""

    run_lines = []

    for key, value in script_env(app_config).items():
        run_lines.append(f'export {key}="{value}"\n')

    log_driver = "" if "fluentd" in app_name or meta.is_local_env() \
        else '--log-driver=fluentd --log-opt tag="docker.{{.ID}}"'
    run_lines.append(f'docker run {log_driver} -d {restart_policy} \\\n')

    params = docker_params(app_config)
    for p in params:
        run_lines.append(f'{p} \\\n')

    run_lines.append(f'--name {app_name} \\\n')
    run_lines.append(f'{tagged_image_name}')

    for rp in docker_run_params(app_config):
        run_lines.append(f" \\\n")
        run_lines.append(rp)

    for ep in app_config.get("container_extra_args", []):
        run_lines.append(f" \\\n")
        run_lines.append(ep)

    return meta.replaced_placeholders_file(run_script_template_path,
                                           run_script_placeholders(comment=comment,
                                                                   app_name=app_name,
                                                                   stop_timeout=stop_timeout,
                                                                   pre_run_cmd=pre_run_cmd(app_config),
                                                                   run_cmd="".join(run_lines),
                                                                   post_run_cmd=post_run_cmd(app_config),
                                                                   zero_downtime_deploy_config=zero_downtime_deploy))


def run_script_placeholders(comment, app_name,
                            stop_timeout,
                            pre_run_cmd,
                            run_cmd,
                            post_run_cmd,
                            zero_downtime_deploy_config):
    placeholders = {
        "comment": comment,
        "app": app_name,
        "stop_timeout": stop_timeout,
        "pre_run_cmd": pre_run_cmd,
        "run_cmd": run_cmd,
        "post_run_cmd": post_run_cmd
    }

    if zero_downtime_deploy_config:
        placeholders['upstream_nginx_dir'] = zero_downtime_deploy_config['upstream_nginx_dir']
        new_app_url_file = path.join(meta.cli_app_package_dir(app_name),
                                     zero_downtime_deploy_config['app_url_file'])
        placeholders['app_url'] = meta.file_content(new_app_url_file).strip()

    return placeholders


def run_template(requires_zero_downtime_deploy):
    if requires_zero_downtime_deploy:
        run_tmpl = "run_zero_downtime.bash"
    else:
        run_tmpl = "run.bash"
    return path.join(meta.cli_templates_dir(), run_tmpl)


def prepared_load_and_run_script(tagged_image_name, docker_image_tar):
    return "\n".join([
        '#!/bin/bash',
        f'echo "Loading {tagged_image_name} image, this can take a while..."',
        f'docker load < {docker_image_tar}',
        'echo "Image loaded, running it..."',
        'exec bash run.bash'
    ])



def script_comments(app_config):
    return app_config.get("comments", [])


def build_cmd(app_config):
    return app_config.get("build_cmd", "")


def pre_run_cmd(app_config):
    return app_config.get("pre_run_cmd", "")


def zero_downtime_deploy_config(app_config):
    return app_config.get("zero_downtime_deploy", {})


def post_run_cmd(app_config):
    return app_config.get("post_run_cmd", "")


def script_env(app_config):
    return app_config.get("script_env", {})


def docker_params(app_config):
    params = []

    for v in app_config.get('volumes', []):
        params.append(f'-v "{v}"')

    for p in app_config.get('ports', []):
        params.append(f'-p "{p}"')

    for k, v in app_config.get('env', {}).items():
        value = sanitized_env_variable(v)
        params.append(f'-e "{k}={value}"')

    memory = app_config.get('memory')
    if memory:
        params.append(f'--memory "{memory}"')

    cpus = app_config.get('cpus')
    if cpus:
        params.append(f'--cpus "{cpus}"')

    network = app_config.get('network')
    if network:
        params.append(f'--network "{network}"')

    hostname = app_config.get("hostname")
    if hostname:
        params.append(f'--hostname "{hostname}"')

    for u in app_config.get('ulimits', []):
        params.append(f'--ulimit "{u}"')

    for ep in app_config.get("docker_extra_args", []):
        params.append(ep)

    return params


def docker_run_params(app_config):
    return app_config.get("run_args", [])


def sanitized_env_variable(var_value):
    str_value = str(var_value)

    # Python will change booleans from json to its representation, which starts with capital letter
    if str_value == 'True' or str_value == 'False':
        return str_value.lower()

    return str_value


app_name = args["app"]
skip_image_export = args["skip_image_export"]

log.info(f"About to read {app_name} config...")

app = meta.app_of_name(app_name)

tag = new_tag(app_name)
log.info(f"Config read, package will be created with a tag: {tag}")

app_package_dir = meta.cli_app_package_dir(app_name)
log.info(f"Prepare target dir: {app_package_dir}...")

if path.exists(app_package_dir):
    shutil.rmtree(app_package_dir)
meta.create_dir(app_package_dir)

log.info("About to build app...")
build_app(app, app_name, tag)

log.info(f"Packaging app...")
package_app(app, app_name, tag, skip_image_export)
