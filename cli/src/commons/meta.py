import logging
import os
import signal
import sys
from argparse import ArgumentParser, SUPPRESS, RawTextHelpFormatter
from os import path
import json
import re
import subprocess as sp
import pathlib

# TODO: change it!
CLI_NAME = "system-cli"

APPS_JSON = "apps.json"

ORDER_KEY = "order"
DEFAULT_APP_ORDER = 99

BLOCKING_OTHERS = "blocking_others"

NAME = "name"
DIR = "dir"
BUILD_CMD = "build_cmd"
BUILD_ACTIONS = "build_actions"
PACKAGE_ACTIONS = "package_actions"

_APPS = None

ENV = "env"
LOCAL_ENV = "local"
_ENV = None

VARS_PATTERN = re.compile("\\${([^}]*)}")
OBJECTS_PATTERN = re.compile('"ob#(.*)"')


def new_log(name=None):
    root_logger = logging.getLogger()
    root_logger.setLevel(level=logging.INFO)

    if not root_logger.handlers:
        console_formatter = logging.Formatter('%(asctime)s:%(levelname)s:%(name)s:%(message)s')
        sh = logging.StreamHandler()
        sh.setFormatter(console_formatter)
        root_logger.addHandler(sh)

    return root_logger if name is None else logging.getLogger(name)


log = new_log('meta')


def cmd_args(args_definitions=None, env=None, prod_env=False, env_arg=True, script_description=None,
             requires_confirm=False):
    if args_definitions is None:
        args_definitions = {}

    if prod_env:
        env = 'prod'

    if script_description:
        formatted_description = f"description:\n{script_description}"
    else:
        formatted_description = None

    parser = _new_args_parser(args_definitions, formatted_description, env=env, env_arg=env_arg,
                              requires_confirm=requires_confirm)

    parsed_args = vars(parser.parse_args())

    global _ENV
    _ENV = parsed_args.get(ENV, env)
    if _ENV is None:
        _ENV = LOCAL_ENV

    if env_arg:
        log.info(f"Running on {_ENV} env")

    _handle_exit_signals()

    return parsed_args


def _new_args_parser(args_definitions, formatted_description, env=None, env_arg=True, requires_confirm=False):
    parser = ArgumentParser(add_help=False, formatter_class=RawTextHelpFormatter, description=formatted_description)

    required_args = parser.add_argument_group('required arguments')
    optional_args = parser.add_argument_group('optional arguments')

    optional_args.add_argument('-h', '--help',
                               action='help',
                               default=SUPPRESS,
                               help='show this help message and exit')

    if env is None and env_arg:
        required_args.add_argument(f'--{ENV}', "-e", help='environment to operate on', required=True)

    if requires_confirm:
        required_args.add_argument("--execute",
                                   help="This is potentially dangerous operation, so we require to pass additional flag",
                                   required=True, action="store_true")

    for k in args_definitions:
        arg_def = args_definitions[k]
        if arg_def.get('required', False):
            required_args.add_argument(f'--{k}', **arg_def)
        else:
            optional_args.add_argument(f'--{k}', **arg_def)

    return parser


def _handle_exit_signals():
    def exit_gracefully(*args):
        print()
        log.info("Exit requested, stopping script.")
        print()
        sys.exit(0)

    signal.signal(signal.SIGINT, exit_gracefully)
    signal.signal(signal.SIGTERM, exit_gracefully)


def multiline_description(*lines):
    return "\n".join(lines)


def root_cli_dir():
    start_cwd = os.getcwd()
    cwd = start_cwd
    for i in range(5):
        files = os.listdir(cwd)
        if 'cli-root' in files:
            return cwd

        cwd, _ = os.path.split(cwd)

    raise Exception(f"Root cli not found, starting from: {start_cwd}")


def root_dir():
    root, _ = path.split(root_cli_dir())
    return root


def config_dir():
    return path.join(root_cli_dir(), "config")


def cli_target_dir():
    return path.join(root_cli_dir(), "target")


def cli_templates_dir():
    return path.join(root_cli_dir(), "templates")


def cli_files_dir():
    cli_dir = path.join(pathlib.Path.home(), f".{CLI_NAME}")

    if not path.exists(cli_dir):
        create_dir(cli_dir)

    return cli_dir


def sorted_apps(reverse=False, names=None):
    global _APPS
    if _APPS is None:
        with open(path.join(config_dir(), APPS_JSON)) as f:
            _APPS = json.load(f)

    ordered = sorted(_APPS, key=lambda a: a.get(ORDER_KEY, DEFAULT_APP_ORDER), reverse=reverse)

    if names:
        ordered = [o for o in ordered if app_name(o) in names]

    return ordered


def app_of_name(app_name):
    for a in sorted_apps():
        if a[NAME] == app_name:
            return a

    raise Exception(f"{app_name} doesn't exist")


def sorted_apps_names(reverse=False, names=None):
    return [app_name(app) for app in
            sorted_apps(reverse=reverse, names=names)]


def app_dir(app):
    return app[DIR]


def app_name(app):
    return app[NAME]


# TODO: refactor
def app_build_cmd(app):
    return app_config(app).get(BUILD_CMD)


def app_build_actions(app):
    return app_config(app).get(BUILD_ACTIONS, [])


def app_package_actions(app):
    return app.get(PACKAGE_ACTIONS, [])


# TODO: fix
def env_config():
    with open(path.join(config_dir(), f'{current_env()}.json')) as f:
        return json.load(f)


def app_config(app):
    config_path = path.join(root_dir(), app_dir(app), "config", f"{current_env()}.json")
    json_file = replaced_placeholders_file(config_path, env_config())
    return json.loads(json_file)


def deploy_config():
    with open(path.join(config_dir(), "deploy.json")) as f:
        return json.load(f)


def current_env():
    return _ENV


def is_local_env():
    return current_env() == LOCAL_ENV


def replaced_placeholders_file(file_path, placeholders_values, objects_values=None):
    with open(file_path) as f:
        with_placeholders_file = f.read()
        return replaced_placeholders_content(with_placeholders_file, placeholders_values, objects_values)


def replaced_placeholders_content(content, placeholders_values, objects_values=None):
    def replace_var(match):
        place_holder = match.group(0)
        value = placeholders_values.get(match.group(1), place_holder)
        return str(value)

    def replace_object(match):
        place_holder = match.group(0)
        value = objects_values.get(match.group(1), place_holder)
        return str(value).replace("'", '"')

    replaced_vars_content = re.sub(VARS_PATTERN, replace_var, content)

    if objects_values:
        replaced_vars_content = re.sub(OBJECTS_PATTERN, replace_object, replaced_vars_content)

    return replaced_vars_content


def execute_script(script):
    code = sp.run(script, shell=True).returncode
    if code != 0:
        log.error(f"Fail to execute script: {script}")
        log.error(f"Exiting: {code}")
        sys.exit(code)


def execute_script_returning_process(script, raise_on_error=True):
    # stdout=sp.PIPE, stderr=sp.PIPE
    process = sp.run(script, shell=True, capture_output=True)
    if raise_on_error and process.returncode != 0:
        log.error(f"Fail to execute script: {script}")
        log.error(f"Exiting: {process}")
        sys.exit(process)

    return process


def execute_script_returning_process_output(script, raise_on_error=True):
    process = execute_script_returning_process(script, raise_on_error=raise_on_error)
    return process.stdout.decode("utf8")


def execute_bash_script(script):
    execute_script(f"""
    #!/bin/bash
    set -e

    {script}
    """)


def create_dir(path, parents=True, exist_ok=True):
    pathlib.Path(path).mkdir(parents=parents, exist_ok=exist_ok)


def prepare_target_dir():
    target_path = path.join(root_cli_dir(), "target")
    create_dir(target_path)
    return target_path
