variable "do_token" {}

variable "region" {
  type    = string
  default = "fra1"
}

variable "droplet_image" {
  type    = string
  default = "ubuntu-22-04-x64"
}

variable "droplet_user" {
  type    = string
  default = "system-template"
}

variable "ssh_keys" {
  type    = list(any)
  default = ["igor", "olek"]
}