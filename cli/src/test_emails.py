import os
import shutil
from os import path

from commons import meta, crypto

log = meta.new_log("test_emails")

args = meta.cmd_args(
    args_definitions={
        "code_variables": {
            "help": meta.multiline_description("Comma separated code variables to be used for email rendering.",
                                               "For what is available, check codeVariables section of templates files.",
                                               "For every lacking variable, random value will be generated.",
                                               "Valid format: code_variables user=12,url=https://url")
        },
        "templates_names": {
            "help": meta.multiline_description("Empty means all. Comma separated names are accepted here.",
                                               "Allowed names are files from templates/email/*.templates folder"),
        },
        "rendering_result_dir": {
            "help": "path to dir with rendering result. Default: target/emails"
        },
        "build_jar": {
            "help": "Whether to build commons before rendering emails. It's not needed if you have them up-to-date",
            "action": "store_true"
        },
        "send_to": {
            "help": meta.multiline_description(
                "Destination to which email should be send. By default they aren't sent, only rendered.",
                "Use special email: test@blackhole.postmarkapp.com to only check emails in the postmark activity log.")
        }
    },
    env_arg=False,
    script_description="""
    Script to render, and optionally send, emails from /templates directory.
    """)

result_dir = args['rendering_result_dir']
if not result_dir:
    target_path = meta.prepare_target_dir()
    result_dir = path.join(target_path, "emails")

    if os.path.exists(result_dir):
        shutil.rmtree(result_dir)

    meta.create_dir(result_dir)

log.info(f"About to render emails to {result_dir} file...")

if args["build_jar"]:
    meta.execute_bash_script(f"""
    cd {meta.root_src_dir()}
    mvn clean install --non-recursive
    cd commons
    mvn clean install -Dmaven.test.skip=true
    """)
else:
    log.info("Not building commons, make sure that you have up to date version of them built!")

print()

code_variables = args["code_variables"]
templates_names = args["templates_names"]
send_to = args['send_to']

if not code_variables:
    domain = "https://codyn.io"
    code_variables = [
        "user=Pierwszy",
        f"oldEmail=codyn@o2.pl",
        f"emailChangeConfirmationUrl={domain}/user-account",
        f"newPasswordUrl={domain}/new-password",
        f"passwordResetUrl={domain}/forgot-password",
        f"code=899Xadkk2ax",
        f"activationUrl={domain}/sign-in",
        f"signUpUrl={domain}/sign-up"
    ]
    code_variables = ",".join(code_variables)

code_variables_export = f'export CODE_VARIABLES="{code_variables}"'
templates_names_export = f"export TEMPLATES_NAMES={templates_names}" if templates_names else ""
send_to_export = f"export SEND_TO={send_to}" if send_to else ""
if send_to:
    secrets = crypto.system_secrets(local_env=False)
    send_to_exports = f"""
export SEND_TO={send_to}
export EMAIL_SERVER_TOKEN="{secrets['postmark-api-token']}"
""".strip()
else:
    send_to_exports = ""

email_renderer_path = path.join(meta.root_src_dir(), "commons", "email")
meta.execute_bash_script(f"""
cd {email_renderer_path}

{code_variables_export}
{templates_names_export}
{send_to_exports}
export RENDERING_RESULT_DIR={result_dir}

exec mvn verify -Dmaven.test.skip=true -PtestEmails
""")

print()
log.info(f"Emails rendered to {result_dir}")
