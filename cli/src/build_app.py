from commons import meta
from commons.tasks import build_app

args = meta.cmd_args({
    "app": {
        "help": "Name of the app",
        "required": True
    },
    "skip_commons": {
        "help": "Skip commons, if you have them built",
        "action": "store_true"
    },
    "skip_commons_tests": {
        "help": "Skip commons tests, if there were no changes there",
        "action": "store_true"
    },
    "skip_tests": {
        "help": "Skip tests, if you don't need their assurance",
        "action": "store_true"
    },
    "skip_image_export": {
        "help": "Don't export final image to tar, which is time-consuming and not needed for local builds (in most cases)",
        "action": "store_true"
    },
    "skip_fluentd_log_driver": {
        "help": "If set, slways skip setting fluentd as logging driver",
        "action": "store_true"
    }
}, script_description="Script to build given app")

build_app.execute(app_name=args["app"],
                  skip_commons=args['skip_commons'],
                  skip_commons_tests=args['skip_commons_tests'],
                  skip_tests=args['skip_tests'],
                  skip_image_export=args['skip_image_export'],
                  skip_fluentd_log_driver=args['skip_fluentd_log_driver'])
