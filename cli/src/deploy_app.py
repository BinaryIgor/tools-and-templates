from commons import meta, crypto
import time
from os import path

log = meta.new_log("deploy_app")

PRE_DEPLOY_ACTIONS = "pre_deploy_actions"
POST_DEPLOY_ACTIONS = "post_deploy_actions"

args = meta.cmd_args(
    args_definitions={
        "app": {
            "help": "Name of the app",
            "required": True
        },
    },
    prod_env=True,
    script_description="Script to deploy given app")


def app_placement(app_name, deploy_config=None):
    if deploy_config is None:
        deploy_config = meta.deploy_config()

    # Machine needs to be in the local etc/hosts!
    return app_machine(app_name, deploy_config['machines'])


def app_machine(app_name, machines):
    app_machine = None

    for m in machines:
        if app_name in m['apps']:
            app_machine = m['name']
            break

    if app_machine is None:
        raise Exception(f"Can't find machine for {app_name} app. Available are: {machines}")

    return app_machine


def app_package_dir(app_name, deploy_config):
    return f"{deploy_config['deploy-dir']}/{app_name}"


def app_latest_package_dir(app_name):
    deploy_config = meta.deploy_config()
    return f'{app_package_dir(app_name, deploy_config)}/latest'


def perform_pre_deploy_actions(remote_host, app, deploy_dir):
    actions = app.get(PRE_DEPLOY_ACTIONS)
    if not actions:
        return

    print()
    log.info("Performing pre deploy actions...")
    perform_deploy_actions(actions, remote_host, deploy_dir)


def perform_deploy_actions(actions, remote_host, deploy_dir):
    for a in actions:
        cmd = meta.replaced_placeholders_content(a, {
            "deploy_dir": deploy_dir
        })
        log.info(f"Executing: {cmd}")
        meta.execute_bash_script(f'ssh {remote_host} "{cmd}"')


def perform_post_deploy_actions(remote_host, app, deploy_dir):
    actions = app.get(POST_DEPLOY_ACTIONS)
    if not actions:
        return

    print()
    log.info("Performing post deploy actions...")
    perform_deploy_actions(actions, remote_host, deploy_dir)


def copy_app_package(remote_host, previous_deploy_dir, latest_deploy_dir, package_dir):
    log.info(f"Copying app package from {package_dir} to {latest_deploy_dir}, this could take a while...")

    meta.execute_bash_script(f"""
      ssh {remote_host} bash -c "'
          rm -f -r {previous_deploy_dir}
          mkdir -p {latest_deploy_dir}
          cp -r {latest_deploy_dir} {previous_deploy_dir}
          rm -r {latest_deploy_dir}
          mkdir {latest_deploy_dir}   
      '"
      scp -r {package_dir}/* {remote_host}:{latest_deploy_dir}
      """)

    print()


def copy_app_secrets_if(app, remote_host):
    log.info("Check if app needs secrets...")
    print()

    secrets_names = meta.app_config(app).get('secrets', [])

    if secrets_names:
        secrets_path = meta.deploy_config()['secrets-path']
        log.info(f"App needs {secrets_names} secrets, copying them to {secrets_path}")

        meta.execute_bash_script(f'ssh {remote_host} "mkdir -p {secrets_path}"')

        secrets = crypto.system_secrets()
        for sn in secrets_names:
            secret = secrets[sn]
            log.info(f"Copying {sn}...")
            meta.execute_bash_script(
                f"""ssh {remote_host} "sudo bash -c 'echo "{secret}" > {secrets_path}/{sn}.txt'" """)
    else:
        log.info("App doesn't need any secrets, skipping them")

    print()


def check_app_status(app_name, remote_host, previous_deploy_dir, multiple_checks=True):
    status = 'failed'
    try:
        status_cmd = f"docker container inspect -f '{{{{.State.Status}}}}' {app_name}"

        log.info("App is deployed, checking its state for a few seconds...")

        checks = 3 if multiple_checks else 1
        elapsed_time = 0
        for i in range(checks):
            time.sleep(5)

            elapsed_time += 5

            status = meta.execute_script_returning_process_output(f'ssh {remote_host} "{status_cmd}"').strip()

            log.info(f"Check {i + 1}/{checks} after {elapsed_time} seconds from start, status: {status}")

            print()
            log.info("Last log:")
            meta.execute_bash_script(f'ssh {remote_host} "docker logs {app_name} --since 30s"')
            print()
    except KeyboardInterrupt:
        log.info("Checking status interrupted on request!")

    print()

    if status == 'running':
        log.info(f"Last status is running, app is up!")
        return True

    log.error(f"""Failed to deploy app, it's status: {status}.
             You should revert to previous version (in: {previous_deploy_dir}) or diagnose a problem""")
    return False


app_name = args['app']

log.info(f"About to deploy {app_name}, reading its config...")
app = meta.app_of_name(app_name)
app_config = meta.app_config(app)

print()
log.info(f"App config:")
print(app_config)
print()

log.info(f"About to deploy {app_name}...")

app_name = meta.app_name(app)

log.info(f"About to deploy {app_name} app, checking where exactly...")

deploy_config = meta.deploy_config()

app_host = app_placement(app_name, deploy_config)

print()
log.info(f"{app_name} will be deployed to {app_host} machine")

deploy_user = deploy_config["user"]
deploy_dir = app_package_dir(app_name, deploy_config)
remote_host = f"{deploy_user}@{app_host}"

previous_deploy_dir = f"{deploy_dir}/previous"
latest_deploy_dir = f"{deploy_dir}/latest"
package_dir = path.join(meta.root_cli_dir(), "target", app_name)

copy_app_secrets_if(app, remote_host)

copy_app_package(remote_host, previous_deploy_dir, latest_deploy_dir, package_dir)

log.info("About to start app....")

perform_pre_deploy_actions(remote_host, app, latest_deploy_dir)

meta.execute_bash_script(f'ssh {remote_host} "cd {latest_deploy_dir}; bash load_and_run.bash"')

deployed = check_app_status(app_name, remote_host, previous_deploy_dir)

if deployed:
    perform_post_deploy_actions(remote_host, app, deploy_dir)

print()
log.info(f"{app_name} is deployed, but check logs to make sure that it runs as desired or run system_status script")
