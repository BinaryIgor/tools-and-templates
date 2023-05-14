terraform {
  required_providers {
    digitalocean = {
      source  = "digitalocean/digitalocean"
      version = "~> 2.0"
    }
  }
  backend "s3" {
    endpoint = "fra1.digitaloceanspaces.com"
    key      = "infra/terraform.tfstate"
    bucket   = "system-template"
    # Fake, not used really, only to satisfy aws cli
    region                      = "us-west-1"
    skip_credentials_validation = true
    skip_metadata_api_check     = true
  }

}

provider "digitalocean" {
  token = var.do_token
}