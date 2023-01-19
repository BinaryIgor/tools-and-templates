from commons import meta, crypto

""""
Generate random secrets to the file.
"""

log = meta.new_log("generate_secrets")

# TODO replace with real secrets keys
SECRETS_NAMES = [
    "db-password",
    "db-reader-password",
    "db-root-password",
    "jwt-token-key"
]

SECRETS_PATH = crypto.secrets_path()
PREDEFINED_SECRETS_PATH = crypto.predefined_secrets_path()

args = meta.cmd_args(
    args_definitions={
        "encryption_password": {
            "required": True,
            "help": "password used to encrypt file with the new secrets"
        },
        "use_predefined": {
            "action": "store_true",
            "help": meta.multiline_description(
                f"If this flag is set, we will take predefined secrets from {PREDEFINED_SECRETS_PATH}.",
                "We will generate new values only for secrets, which are not present there.",
                "All additional secrets from this fle (not managed by this generator), ",
                "will be encrypted and saved together with generated ones.",
                "Required format is key=value, with single pair per line")
        }
    },
    prod_env=True,
    script_description=f"""Generate random secrets (using predefined if set) to the encrypted, 
        with a given key, file: {SECRETS_PATH}""")

log.info(f"Generating the following secrets to the file: {SECRETS_NAMES}")
log.info(f"Passwords are random alphanumeric characters of {crypto.PASSWORD_LENGTH} length.")
log.info(f"Keys are random {crypto.KEYS_BYTES_LENGTH} bytes encoded to Base64")

print()
log.info("Generating secrets...")
print()

secrets_map = {}

if args['use_predefined']:
    with open(PREDEFINED_SECRETS_PATH) as f:
        for line in f.readlines():
            kv = line.split("=", 1)
            key = kv[0].strip()
            secrets_map[key] = kv[1].strip()

            log.info(f"Using predefined value for {key} secret")

for sn in SECRETS_NAMES:
    if sn not in secrets_map:
        log.info(f"Generating {sn} secret..")
        secret_value = crypto.random_password() if sn.endswith("password") else crypto.random_key()
        secrets_map[sn] = secret_value

log.info("Secrets generated, encrypting them...")

encrypted_secrets = crypto.encrypted_secrets(secrets_map, password=args['encryption_password'])

log.info(f"Secrets encrypted, saving them to: {SECRETS_PATH}")

with open(SECRETS_PATH, "wb") as f:
    f.write(encrypted_secrets)

log.info("Secrets saved")
