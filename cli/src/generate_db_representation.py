import os

from commons import meta, crypto
from os import path

log = meta.new_log("generate_db_representation")

args = meta.cmd_args({
    "schemas": {
        "help": "Comma separated schemas to migrate. Default are empty, which means all"
    }
}, script_description="Script to generate jooq code representation for given db/schema(s)", env_arg=False)

schemas_arg = args["schemas"]

if schemas_arg:
    schemas = [s.strip() for s in schemas_arg.split(",")]
else:
    schemas_dir = path.join(meta.db_schemas_dir())
    schemas = [d for d in os.listdir(schemas_dir) if path.isdir(path.join(schemas_dir, d))]

log.info(f"Generating db representation for schemas {schemas}...")
print()

env_config = meta.env_config()
db_url = env_config['db-url']
db_user = env_config['db-user']
db_password = crypto.system_secrets()[f'db-password']

root_code_path = path.join(meta.root_code_dir(), "commons", "sql-db")
for s in schemas:
    log.info(f"Generating representation for {s} schema...")

    generator_path = path.join(root_code_path, "schema", s)

    meta.execute_bash_script(f"""
    cd {generator_path}
    exec mvn jooq-codegen:generate -Djooq.codegen.jdbc.url="{db_url}" \\
      -Djooq.codegen.jdbc.user="{db_user}" \\
      -Djooq.codegen.jdbc.password="{db_password}"
    """)
    print()

print()
log.info("All representations generated")
