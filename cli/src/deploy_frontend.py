from commons import meta
from os import path
from commons.infra import machines

log = meta.new_log("deploy_frontend")

ADMIN_DIR = "__admin__"
REMOTE_TMP_PACKAGE_DIR = "/tmp/frontend"

args = meta.cmd_args(
    args_definitions={
        "package_path": {
            "help": "Path to deployable frontend package",
            "required": True
        },
        "type": {
            "help": "Type of frontend to deploy",
            "choices": ["app", "admin"],
            "required": True
        }
    },
    prod_env=True,
    script_description="Script to deploy new frontend version (app or admin) to nginx")

package_path = args['package_path']
app_type = args['type']

static_dir = meta.env_config()['static-path']
admin_static_dir = path.join(static_dir, ADMIN_DIR)
prev_static_dir = f'{static_dir}-prev'

deploy_user = meta.deploy_config()['user']
deploy_ip = machines.data_of_machine("front-api")[machines.PUBLIC_IP]
remote_host = f'{deploy_user}@{deploy_ip}'

log.info(f"Deploying new {app_type} frontend to {static_dir} of {remote_host} nginx")
log.info(f"Package source: {package_path}")

print()
log.info("Copying package to tmp remote directory...")
meta.execute_bash_script(f"ssh {remote_host} 'mkdir -p {REMOTE_TMP_PACKAGE_DIR}'")
meta.execute_bash_script(f"scp -r {package_path}/* {remote_host}:{REMOTE_TMP_PACKAGE_DIR}")

print()

log.info("Package copied, preparing target remote directories....")
print()

# Sudo is needed in some places, because nginx is managing these folders. It runs in docker as root
if app_type == 'admin':
    prev_admin_static_dir = f'{prev_static_dir}/{ADMIN_DIR}'
    meta.execute_bash_script(f"""ssh {remote_host} "
        mkdir -p {REMOTE_TMP_PACKAGE_DIR}
        if [ -d {admin_static_dir} ]; then
            echo "Moving {admin_static_dir} to {prev_admin_static_dir}..."
            rm -r -f {prev_admin_static_dir}
            cp -r {admin_static_dir} {prev_admin_static_dir}
            sudo rm -r {admin_static_dir}/*
            echo "Previous admin package moved"
        else
          sudo mkdir -p {admin_static_dir}
        fi
        "
    """)
    package_target = admin_static_dir
else:
    meta.execute_bash_script(f"""ssh {remote_host} "
        mkdir -p {REMOTE_TMP_PACKAGE_DIR}
        if [ -d {static_dir} ]; then
            echo "Moving {static_dir} to {prev_static_dir}..."
            rm -r -f {prev_static_dir}
            cp -r {static_dir} {prev_static_dir}
        else
          mkdir {static_dir}
        fi
    "
    """)

    not_admin_files = meta.execute_script_returning_process_output(
        f'''ssh {remote_host} "ls '{static_dir}' | grep -v '{ADMIN_DIR}'"''',
        raise_on_error=False).strip()

    if not_admin_files:
        # Delete everything besides admin dir
        files_to_remove = [f'{static_dir}/{f}' for f in not_admin_files.split("\n")]
        for f in files_to_remove:
            print(f"Removing previous {f}...")
            meta.execute_bash_script(f"""ssh {remote_host} 'sudo rm -r "{f}"'""")
    else:
        log.info("No previous files to remove")

    package_target = static_dir

print()
log.info(f"All directory prepared, moving package from {REMOTE_TMP_PACKAGE_DIR} to {package_target}...")
print()

meta.execute_bash_script(f'ssh {remote_host} "sudo mv {REMOTE_TMP_PACKAGE_DIR}/* {package_target}"')

print()

log.info(f"Frontend of {app_type} type deployed to {package_target} of {remote_host} machine.")
