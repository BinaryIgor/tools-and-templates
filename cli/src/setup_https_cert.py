from commons import meta

"""
Webroot mode: certbot will create challenge file in the specified webroot to verify domain.
Make sure that given webroot-path is exposed via your http server!

https://eff-certbot.readthedocs.io/en/stable/using.html#webroot
The webroot plugin works by creating a temporary file for each of your requested domains in: 
${webroot-path}/.well-known/acme-challenge.
Then the Let’s Encrypt validation server makes HTTP requests to validate that the DNS for each requested domain
resolves to the server running certbot. An example request made to your web server would look like:
66.133.109.36 - - [05/Jan/2016:20:11:24 -0500] "GET /.well-known/acme-challenge/HGr8U1IeTW4kY_Z6UIyaakzOkyQgPr_7ArlLgtZE8SX HTTP/1.1" 200 87 "-" "Mozilla/5.0 (compatible; Let's Encrypt validation server; +https://www.letsencrypt.org)"

You can check renewal by running: sudo certbot renew --dry-run.
For some reasons, it hangs in below script.
"""

log = meta.new_log("setup_https_cert")

args = meta.cmd_args(
    env="prod",
    args_definitions={
        "domain": {
            "required": True
        },
        "domain_email": {
            "required": True,
            "help": "email associated with the domain"
        },
        "webroot_path": {
            "help": "Path to webroot, where certbot will be able to place challenge files"
        }
    },
    script_description="""Script to setup https certs for a given domain"""
)

deploy_user = meta.deploy_config()['user']
domain = args["domain"]
domain_email = args["domain_email"]
webroot_path = args["webroot_path"]
if not webroot_path:
    webroot_path = meta.env_config()["static-path"]

log.info(f"Setting up certbot and http cert..")
log.info(f"Domain: {domain}, email: {domain_email}, and a webroot: {webroot_path}")

remote_host = f"{deploy_user}@{domain}"

meta.execute_bash_script(f"""
ssh {remote_host} "
echo "Setting up certbot..."
sudo snap install --classic certbot
sudo ln -s /snap/bin/certbot /usr/bin/certbot
echo
echo "Certbot configured, generating certs..."
sudo certbot certonly --webroot --webroot-path "{webroot_path}" --email "{domain_email}" --domains "{domain} --non-interactive --agree-tos -v"
"
""")

print()
log.info(f"Https setup for {domain} domain with automatic renewal!")
