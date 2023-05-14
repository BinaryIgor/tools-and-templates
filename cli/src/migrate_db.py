from commons import meta, crypto, db
from os import path

SCHEMAS = "schemas"
FLYWAY_IMAGE = "flyway/flyway:9-alpine"

log = meta.new_log("migrate_db")

args = meta.cmd_args({
    "schemas": {
        "help": "Comma separated schemas to migrate. Default are empty, which means all"
    },
    "repair": {
        "help": meta.multiline_description("Whether to repair migrations.",
                                           "It means that their checksums will be recalculated",
                                           "with the assumption that changes were correctly executed already."),
        "action": "store_true"
    }
}, script_description="Script to migrate db schemas")

schemas = args['schemas']

if not schemas:
    schemas_order = meta.file_content(path.join(meta.db_schemas_dir(), "schemas_order.txt"))
    schemas = schemas_order.split(" ")

log.info(f"About to migrate {schemas} schemas...")

if meta.is_local_env():
    log.info("Local env, initialize db first...")
    meta.execute_bash_script("python3 init_db.py --env local")

env_config = meta.env_config()

db_port = env_config['db-port']

jdbc_url = env_config['db-local-url']
db_user = env_config['db-user']
db_reader_user = f'{db_user}_reader'
db_name = env_config['db-name']

secrets = crypto.system_secrets()
db_password = secrets['db-password']
db_reader_password = secrets['db-reader-password']
db_root_password = secrets['db-root-password']

repair = args['repair']

print()

schemas_dir = meta.db_schemas_dir()

for s in schemas:
    cmd = "repair" if repair else "migrate"
    schema_dir = path.join(meta.db_schemas_dir(), s)

    log.info(f"{cmd} {s} schema from path: {schema_dir}..")

    meta.execute_bash_script(f"""
    docker run --rm -v "{schema_dir}:/flyway/sql" \
        --network host \
        {FLYWAY_IMAGE} -url="{jdbc_url}" \
        -schemas="{s}" \
        -user="{db_user}" \
        -password="{db_password}" \
        {cmd}
    """)

    print()

print()

log.info(f"Migrations run, reissue reading permissions for user: {db_reader_user}")

connection = db.root_connection(db_name=db_name)

db.grant_read_privileges(connection.cursor(), db_reader_user, db_name, schemas="all")

log.info("Read permissions given.")
