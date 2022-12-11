from commons import meta, crypto

log = meta.new_log("decrypt_secrets")

args = meta.cmd_args({
    "password": {
        "help": f"Password to decrypt file, by default taken from {crypto.CLI_SECRETS_PASSWORD_ENV} env"
    },
    "secrets_path": {
        "help": f"Path to encrypted file. Default: {crypto.secrets_path()}",
        "default": crypto.secrets_path()
    }
}, env_arg=False, requires_confirm=True)

secrets_path = args["secrets_path"]

log.info(f"Decrypting secrets from {secrets_path} to the console")

with open(secrets_path, "rb") as f:
    data = f.read()

log.info("Data read, decrypting....")

decrypted = crypto.decrypted_data(data, password=args.get("password")).decode("utf8")

log.info("Decrypted data:\n")
for d in decrypted.split("\n"):
    print(d)
