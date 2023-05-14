data "digitalocean_ssh_keys" "keys" {
  filter {
    key    = "name"
    values = var.ssh_keys
  }
}