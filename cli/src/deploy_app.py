from commons import meta
from commons.tasks import deploy_app

args = meta.cmd_args(args_definitions={
    "app": {
        "help": "Name of the app",
        "required": True
    },
    "machines": {
        "help": meta.multiline_description("comma separated list of machines to deploy app.",
                                           "By default, app will be deployed to all machines configured in deploy.json file")
    },
    "copy_only": {
        "help": "Only copy new app package, do not run it. Useful for special cases/tests/debugging",
        "action": "store_true"
    }
}, prod_env=True, script_description="Script to deploy given app")

deploy_app.execute(app_name=args['app'],
                   machines=meta.str_arg_as_list(args['machines']),
                   copy_only=args['copy_only'])
