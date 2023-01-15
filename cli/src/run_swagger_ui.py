from commons import meta
from os import path

SWAGGER_PORT = 9999
swagger_dir = path.join(meta.root_tools_dir(), "swagger")

meta.execute_bash_script(f"""
cd {swagger_dir}
echo "Running swagger from {swagger_dir} dir on a port: {SWAGGER_PORT}"
export SWAGGER_PORT={SWAGGER_PORT}
bash build_and_run.bash
""")
