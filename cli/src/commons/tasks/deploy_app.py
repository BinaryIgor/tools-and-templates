import sys
import time
from os import path

from commons import meta, crypto
from commons.infra import machines as infra_machines

# TODO: incompatible changes for many instances deployment?

CI_MACHINE_NAME = "CI_MACHINE_NAME"
CI_DEPLOY_DIR = "CI_DEPLOY_DIR"

DOCKER_FAILED_STATUS = 'failed'
DOCKER_RUNNING_STATUS = 'running'

DEPLOY_ENV_FILE = "deploy.env"

log = meta.new_log("deploy_app")


def execute(app_name, machines=None, copy_only=False):
    log.info(f"About to deploy {app_name}, reading its config...")
    app = meta.app_of_name(app_name)
    app_config = meta.app_config(app)

    print()
    log.info(f"App config:")
    print(app_config)
    print()

    log.info(f"About to deploy {app_name} app, checking where exactly...")

    deploy_config = meta.deploy_config()

    app_hosts = _app_placements(app_name, deploy_config,
                                to_filter_machines=machines)

    successful_deploys = []
    failed_deploys = []

    for ah, ip in app_hosts.items():
        try:
            print()
            log.info(f"Deploying {app_name} to {ah}:{ip} machine")

            deploy_user = deploy_config["user"]
            deploy_dir = _app_package_dir(app_name, deploy_config)
            remote_host = f"{deploy_user}@{ip}"

            previous_deploy_dir = f"{deploy_dir}/previous"
            latest_deploy_dir = f"{deploy_dir}/latest"
            package_dir = path.join(meta.root_cli_dir(), "target", app_name)

            _copy_app_secrets_if(app, remote_host, deploy_config)
            _copy_app_package(machine_name=ah,
                              remote_host=remote_host,
                              previous_deploy_dir=previous_deploy_dir,
                              latest_deploy_dir=latest_deploy_dir,
                              package_dir=package_dir)

            if copy_only:
                print("...")
                log.info("Copy only deploy, skipping app run!")
            else:
                log.info("About to start an app...")

                _perform_pre_deploy_actions(app_config=app_config,
                                            deploy_dir=latest_deploy_dir,
                                            machine_name=ah,
                                            remote_host=remote_host)

                meta.execute_bash_script(f'ssh {remote_host} "cd {latest_deploy_dir}; bash load_and_run.bash"')

                _check_app_status(app_name=app_name, remote_host=remote_host)

            successful_deploys.append(ah)
        except KeyboardInterrupt:
            log.info("Deployment interrupted on a request!")
        except Exception:
            log.exception(f"Failed to deploy to {ah}")
            failed_deploys.append(ah)

    print()
    log.info(
        f"{app_name} is deployed on {len(successful_deploys)} hosts, but check logs to make sure that it runs as desired or run system_status script")

    if failed_deploys:
        print()
        log.error(f"Failed to deploy app to {failed_deploys} hosts")
        sys.exit(1)


def _app_placements(app_name, deploy_config, to_filter_machines):
    deploy_machines = deploy_config['machines']
    if to_filter_machines:
        deploy_machines_names = [d['name'] for d in deploy_machines]
        machines = [m for m in deploy_machines_names if m in to_filter_machines]
        if not machines:
            raise Exception(f"There are no machines for this app matching {to_filter_machines} filter")
    else:
        machines = _app_machines(app_name, deploy_machines)

    print()
    log.info(f"{app_name} will be deployed to {machines} machines")

    return {m['name']: m['public_ip'] for m in infra_machines.data(to_filter_names=machines)}


def _app_machines(app_name, machines):
    app_m = []
    for m in machines:
        if app_name in m['apps']:
            app_m.append(m['name'])

    if not app_m:
        raise Exception(f"Can't find any machine for {app_name} app")

    return app_m


def _app_package_dir(app_name, deploy_config):
    return f"{deploy_config['deploy-dir']}/{app_name}"


def _app_latest_package_dir(app_name, deploy_config):
    return f'{_app_package_dir(app_name, deploy_config)}/latest'


def _perform_pre_deploy_actions(app_config, machine_name, remote_host, deploy_dir):
    actions = app_config.get("pre_deploy_actions")
    if not actions:
        return

    print()
    log.info("Performing pre deploy actions...")
    _perform_deploy_actions(actions,
                            machine_name=machine_name,
                            remote_host=remote_host,
                            deploy_dir=deploy_dir)


def _perform_deploy_actions(actions, machine_name, remote_host, deploy_dir):
    for a in actions:
        cmd = meta.replaced_placeholders_content(a, {
            CI_MACHINE_NAME: machine_name,
            CI_DEPLOY_DIR: deploy_dir,
        })
        log.info(f"Executing: {cmd}")
        meta.execute_bash_script(f'ssh {remote_host} "{cmd}"')


def _copy_app_package(machine_name, remote_host, previous_deploy_dir, latest_deploy_dir, package_dir):
    log.info(f"Copying app package from {package_dir} to {latest_deploy_dir}, this could take a while...")

    local_deploy_env_file_path = path.join(meta.prepare_target_dir(), DEPLOY_ENV_FILE)
    remote_deploy_env_file_path = path.join(latest_deploy_dir, DEPLOY_ENV_FILE)

    with open(local_deploy_env_file_path, "w") as f:
        f.write(f"""
            export {CI_MACHINE_NAME}="{machine_name}"
        """.strip())

    meta.execute_bash_script(f"""
        ssh {remote_host} bash -c "'
            rm -f -r {previous_deploy_dir}
            mkdir -p {latest_deploy_dir}
            cp -r {latest_deploy_dir} {previous_deploy_dir}
            rm -r {latest_deploy_dir}
            mkdir {latest_deploy_dir}
        '"
        scp {local_deploy_env_file_path} {remote_host}:{remote_deploy_env_file_path}
        scp -r {package_dir}/* {remote_host}:{latest_deploy_dir}
    """, exit_on_failure=False)


def _copy_app_secrets_if(app, remote_host, deploy_config):
    log.info("Check if app needs secrets...")
    print()

    secrets_names = meta.app_config(app).get('secrets', [])

    if secrets_names:
        secrets_path = deploy_config['secrets-path']
        log.info(f"App needs {secrets_names} secrets, copying them to {secrets_path}")

        meta.execute_bash_script(f'ssh {remote_host} "mkdir -p {secrets_path}"', exit_on_failure=False)

        secrets = crypto.system_secrets()
        for sn in secrets_names:
            secret = secrets[sn]
            log.info(f"Copying {sn}...")
            meta.execute_bash_script(f"""
                ssh {remote_host} "sudo bash -c 'echo "{secret}" > {secrets_path}/{sn}.txt'"
            """, exit_on_failure=False)
    else:
        log.info("App doesn't need any secrets, skipping them")

    print()


def _check_app_status(app_name, remote_host, multiple_checks=True):
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
            meta.execute_bash_script(f'ssh {remote_host} "docker logs {app_name} --since 90s"')
            print()
    except KeyboardInterrupt:
        log.info("Checking status interrupted on request!")
