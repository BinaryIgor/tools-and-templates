import logging
import os
import signal
import sys
from argparse import ArgumentParser, SUPPRESS, RawTextHelpFormatter
from os import path

ENV = "env"
LOCAL_ENV = "local"
_ENV = None


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


def cmd_args(args_definitions=None, env=LOCAL_ENV, prod_env=False, env_arg=True, script_description=None,
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
