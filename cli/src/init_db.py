from commons import meta, db, crypto
import time

log = meta.new_log("init_db")

args = meta.cmd_args(
    args_definitions={
        "init_root_password": {
            "help": "initial root password, if not given, it's taken from secrets",
            "default": "postgres"
        }
    },
    script_description="Script to init db. Passwords source for users: crypto.system_secrets()")

env_config = meta.env_config()

secrets = crypto.system_secrets()

root_user = env_config['db-root-user']
root_password = secrets['db-root-password']
initial_root_password = args['init_root_password']

trials = 5

for i in range(trials):
    try:
        try:
            conn = db.root_connection(root_user, initial_root_password)
        except Exception:
            log.info("Failed to use initial root password, will use next one instead")
            conn = db.root_connection(root_user, root_password)
            break
    except Exception:
        if (i + 1) < trials:
            log.warning("Can't connect to db, will retry in 1s")
            time.sleep(1)
        else:
            raise Exception(f"Failed to connect to db in {trials} trials")

cur = conn.cursor()

db_name = env_config['db-name']
db_user = env_config['db-user']

db.create_db_if_does_not_exist(cur, db_name)
db.create_user_if_does_not_exist(cur, db_user, secrets['db-password'], db_name, privileges="ALL")

db_reader_user = f'{db_user}_reader'
db.create_user_if_does_not_exist(cur, db_reader_user, secrets['db-reader-password'], db_name)

log.info(f"{db_user} and {db_reader_user} users for {db_name} created, rotating root password...")

if meta.is_local_env():
    # Not possible to change root password on digital ocean like that!
    db.alter_user_password(cur, root_user, secrets['db-root-password'])

log.info("Db initialized")
