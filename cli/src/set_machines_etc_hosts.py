from os import path

from commons import meta
from commons.infra import machines

ETC_HOSTS_FILES = ["/etc/hosts", "/etc/cloud/templates/hosts.debian.tmpl"]

CUSTOM_HOSTS_HEADER = "#custom-hosts"

log = meta.new_log("set_machines_etc_hosts")

args = meta.cmd_args(prod_env=True,
                     requires_confirm=True,
                     args_definitions={
                         "to_delete_hosts": {
                             "help": "comma separated additional hosts to delete"
                         },
                         "dry_run": {
                             "help": "Only print hosts config, do not execute. Useful for debugging/setting up hosts on local machine",
                             "action": "store_true"
                         }
                     },
                     script_description="Script to setup etc hosts for all machines")


def prepared_hosts(machines_hosts, machines_private_ips):
    hosts = {}

    for m in machines_hosts:
        ip = machines_private_ips[m]
        for mh in machines_hosts[m]:
            hosts[mh] = ip

    return hosts


def prepare_change_hosts_script(hosts, additional_to_delete_hosts, etc_hosts_file_idx):
    etc_hosts_file = ETC_HOSTS_FILES[etc_hosts_file_idx]

    log.info(f"Preparing etc hosts script ({etc_hosts_file}) for droplets from {hosts}")

    sed_lines_removal = []
    to_add_hosts_lines = [CUSTOM_HOSTS_HEADER]

    for host, ip in hosts.items():
        sed_lines_removal.append(f"sed -i '/{host}/d' {etc_hosts_file}")
        to_add_hosts_lines.append(f"{ip} {host}")

    for host in additional_to_delete_hosts:
        sed_lines_removal.append(f"sed -i '/{host}/d' {etc_hosts_file}")

    sed_lines_removal.append(f"sed -i '/{CUSTOM_HOSTS_HEADER}/d' {etc_hosts_file}")
    to_add_hosts_lines.append(CUSTOM_HOSTS_HEADER)

    sed_replacement = "\n".join(sed_lines_removal)
    custom_hosts = "\n".join(to_add_hosts_lines)

    target_dir = meta.prepare_target_dir()
    custom_hosts_path = path.join(target_dir, f"change_hosts_{etc_hosts_file_idx}.bash")

    print()
    log.info("Hosts to set:")
    print(custom_hosts)
    print()

    with open(custom_hosts_path, "w") as f:
        change_hosts_script = ["#!/bin/bash\n",
                               f"{sed_replacement}\n",
                               f'echo "{custom_hosts}" >> {etc_hosts_file}']

        f.writelines(change_hosts_script)

    return custom_hosts_path


def change_hosts(machine_user, machines_public_ips, script_path):
    for m, ip in machines_public_ips.items():
        print()
        log.info(f"Updating {m} machine hosts...")

        remote_host = f"{machine_user}@{ip}"
        meta.execute_bash_script(f'cat {script_path} | ssh {remote_host} "sudo bash"')

        log.info(f"{m} hosts updated")
        print()


to_delete_hosts = meta.str_arg_as_list(args["to_delete_hosts"])
dry_run = args['dry_run']

log.info("Setting up etc hosts for machines, getting config...")

deploy_config = meta.deploy_config()

machines_hosts = {m['name']: m.get('hosts', []) for m in deploy_config['machines']}

log.info("Config read, required hosts for machines")
for m in machines_hosts:
    log.info(f"{m}: {machines_hosts[m]}")

log.info(f"Getting machines data...")

machines_data = machines.data(to_filter_names=machines_hosts.keys())
machines_public_ips = machines.public_ips(machines_data)
machines_private_ips = machines.private_ips(machines_data)

print()
log.info("Preparing hosts...")
hosts = prepared_hosts(machines_hosts, machines_private_ips)

change_script_paths = []
for i in range(len(ETC_HOSTS_FILES)):
    change_script_path = prepare_change_hosts_script(hosts, additional_to_delete_hosts=to_delete_hosts,
                                                     etc_hosts_file_idx=i)
    change_script_paths.append(change_script_path)

if dry_run:
    print()
    log.info("Dry run, exiting!")
else:
    print()
    log.info("Script ready, changing machines hosts")
    for s in change_script_paths:
        change_hosts(deploy_config['user'], machines_public_ips, s)
    print()
    log.info("Hosts updated")
