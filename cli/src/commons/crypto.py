import base64
import os
import secrets
import string
from os import path

from cryptography.fernet import Fernet

from commons import meta

KEYS_BYTES_LENGTH = 32
PASSWORD_LENGTH = 48
PASSWORD_CHARACTERS = f'{string.ascii_letters}{string.digits}'

# TODO: change its name!
CLI_SECRETS_PASSWORD_ENV = "CLI_SECRETS_PASSWORD"


def secrets_path():
    return path.join(meta.cli_files_dir(), "secrets")


def predefined_secrets_path():
    return path.join(meta.cli_files_dir(), "predefined-secrets")


def decrypted_secrets_dir():
    return path.join(meta.cli_files_dir(), "decrypted-secrets")


def random_key():
    return base64.b64encode(os.urandom(KEYS_BYTES_LENGTH)).decode("ascii")


def random_password():
    characters = list(PASSWORD_CHARACTERS)
    return ''.join(secrets.choice(characters) for _ in range(PASSWORD_LENGTH))


def encrypted_data(password, data):
    key = _password_to_fernet_key(password)
    return Fernet(key).encrypt(data)


def encrypted_secrets(secrets_map, password=None):
    password = _given_or_from_env_password(password)

    secrets_lines = []
    for k in secrets_map:
        secrets_lines.append(f"{k}={secrets_map[k]}")

    secrets_bytes = "\n".join(secrets_lines).encode("utf-8")

    return encrypted_data(password, secrets_bytes)


def _given_or_from_env_password(password):
    if password is None:
        password = os.environ.get(CLI_SECRETS_PASSWORD_ENV)
        if password is None:
            raise Exception(f"Both given and from {CLI_SECRETS_PASSWORD_ENV} env passwords are null")

    return password


def _password_to_fernet_key(password):
    key = password
    lacking_key_chars = 32 - len(key)
    for i in range(lacking_key_chars):
        key += '0'

    return base64.urlsafe_b64encode(key.encode("utf8"))


def decrypted_data(data, password=None):
    password = _given_or_from_env_password(password)
    key = _password_to_fernet_key(password)
    return Fernet(key).decrypt(data)


def system_secrets(local_env=None):
    if local_env is False or not meta.is_local_env():
        password = os.environ.get(CLI_SECRETS_PASSWORD_ENV)
        if password is None:
            raise Exception(
                f"For non local env secrets password is required, set in {CLI_SECRETS_PASSWORD_ENV} env variable")

        print(f"Taking encrypted secrets from: {secrets_path()}")

        with open(secrets_path(), "rb") as f:
            data = f.read()

        decrypted = decrypted_data(data, password=password).decode("utf8")

        secrets_values = {}

        for l in decrypted.split("\n"):
            kv = l.split("=", maxsplit=1)
            secrets_values[kv[0].strip()] = kv[1].strip()

        return secrets_values

    return local_system_secrets()


def local_system_secrets():
    local_secrets_dir = path.join(meta.config_dir(), "local-secrets")

    secrets_values = {}

    for fn in os.listdir(local_secrets_dir):
        key = fn.replace(".txt", "")
        with open(path.join(local_secrets_dir, fn)) as f:
            value = f.read()

        secrets_values[key] = value

    return secrets_values
