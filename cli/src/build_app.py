from commons import meta, crypto
from datetime import datetime
from os import path
import shutil

SECRETS_BUILD_ENV_PREFIX = "secrets:"
BUILD_ENV = "build_env"
LOAD_AND_RUN_SCRIPT = "load_and_run.bash"

LAST_DOCKER_LOGS_COLLECTOR_READ_AT_FILE = "/tmp/docker-logs-collector-last-data-read-at.txt"
LAST_DOCKER_LOGS_COLLECTOR_READ_AT_FILE_VARIABLE = "last_docker_logs_collector_read_at_file"

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

    build_cmd = meta.app_build_cmd(app)
    if build_cmd:
        log.info(f"Executing build cmd with app env: {build_cmd}")

        app_config = meta.app_config(app)

        meta.execute_bash_script(f"""
            cd {app_dir}
            {app_build_env_exports_str(app_config)}
            {build_cmd}
        """)
        log.info("build cmd executed")

    meta.execute_bash_script(f"""
    cd {app_dir}    
    docker build . -t {app_name}:{tag}""")


def app_build_env_exports_str(app_config):
    env = app_config.get(BUILD_ENV, {})
    exports = []

    for k, v in env.items():
        v_str = str(v)
        if v_str.startswith(SECRETS_BUILD_ENV_PREFIX):
            secret = v_str.replace(SECRETS_BUILD_ENV_PREFIX, "")
            value = crypto.system_secrets()[secret]
        else:
            value = v

        exports.append(f"export {k}={value}")

    return "\n".join(exports)


def package_app(app, app_name, tag, skip_image_export=False):
    log.info(f"About to create a package for {app_name} app...")

    log.info("Getting app config...")
    app_config = meta.app_config(app)
    log.info("App config loaded")

    tagged_image_name = f'{app_name}:{tag}'

    app_package_dir = path.join(meta.cli_target_dir(), app_name)

    log.info(f"Prepare target dir: {app_package_dir}...")

    if path.exists(app_package_dir):
        shutil.rmtree(app_package_dir)
    meta.create_dir(app_package_dir)

    decrypt_needed_locally_secrets(app_config)

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
    run_script_template_path = path.join(meta.cli_templates_dir(), "run_template.bash")
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

    prep_run_cmd = prepare_run_cmd(app_config)
    if prep_run_cmd:
        run_lines.append(f'{prep_run_cmd}\n')

    run_lines.append(f'exec docker run -d {restart_policy} \\\n')

    params = docker_params(app_config)
    for p in params:
        run_lines.append(f'{p} \\\n')

    run_lines.append(f'--name {app_name} \\\n')
    run_lines.append(f'{tagged_image_name}')

    for rp in docker_run_params(app_config):
        run_lines.append(f" \\\n")
        run_lines.append(rp)

    return meta.replaced_placeholders_file(run_script_template_path, {
        "comment": comment,
        "app": app_name,
        LAST_DOCKER_LOGS_COLLECTOR_READ_AT_FILE_VARIABLE: LAST_DOCKER_LOGS_COLLECTOR_READ_AT_FILE,
        "stop_timeout": stop_timeout,
        "should_wait": should_wat_for_last_logs_collection(),
        "run_cmd": "".join(run_lines)
    })


def should_wat_for_last_logs_collection():
    return "not" if meta.is_local_env() else "should_wait"


def prepared_load_and_run_script(tagged_image_name, docker_image_tar):
    return "\n".join([
        '#!/bin/bash',
        f'echo "Loading {tagged_image_name} image, this can take a while..."',
        f'docker load < {docker_image_tar}',
        'echo "Image loaded, running it..."',
        'exec bash run.bash'
    ])


def decrypt_needed_locally_secrets(app_config):
    decrypted_secrets = app_config.get("decrypted_secrets")
    if not decrypted_secrets:
        return

    print()
    log.info(f"Decrypting {decrypted_secrets} secrets for local app usage")
    print()

    decrypted_secrets_dir = crypto.decrypted_secrets_dir()
    meta.create_dir(decrypted_secrets_dir)

    decrypted_secrets_values = crypto.system_secrets()

    for s in decrypted_secrets:
        with open(path.join(decrypted_secrets_dir, f"{s}.txt"), "w") as f:
            secret = decrypted_secrets_values[s]
            f.write(secret)

    log.info(f"Decrypted secrets saved to {decrypted_secrets_dir}")
    print()


def copy_file_from_app_target_to_package(app, app_package_dir, to_copy_file):
    full_file_path = path.join(meta.root_dir(), meta.app_dir(app), "target", to_copy_file)
    log.info(f"Copying file: {full_file_path} to deploy target")

    new_path = path.join(app_package_dir, to_copy_file)

    if path.isdir(full_file_path):
        shutil.copytree(full_file_path, new_path)
    else:
        shutil.copy2(full_file_path, new_path)

    return new_path


def script_comments(app_config):
    return app_config.get("comments", [])


def prepare_run_cmd(app_config):
    return app_config.get("prepare_run_cmd", "")


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

log.info("About to build app...")
build_app(app, app_name, tag)

log.info(f"Packaging app...")
package_app(app, app_name, tag, skip_image_export)
