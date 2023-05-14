# Terraform infra setup

## Dependencies
* aws cli for infra state management: https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html#getting-started-install-instructions
* aws cli setup
    * run: `aws configure --profile digitalocean`
    * activate profile before running terraform commands: `export AWS_PROFILE=digitalocean`