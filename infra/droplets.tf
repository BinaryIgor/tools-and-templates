locals {
  ssh_keys = [for d in data.digitalocean_ssh_keys.keys.ssh_keys : d.id]
  user_data = templatefile("droplet_init.bash", {
    USERNAME : var.droplet_user,
    INSTALL_PROMETHEUS_NODE_EXPORTER : "true"
  })
  all_ip_adresses = ["0.0.0.0/0", "::/0"]
}

resource "digitalocean_reserved_ip" "front_api" {
  region = var.region
}

resource "digitalocean_reserved_ip" "socket_server" {
  region = var.region
}

resource "digitalocean_reserved_ip" "turn_server_1" {
  region = var.region
}

resource "digitalocean_droplet" "monitor" {
  image      = var.droplet_image
  name       = "monitor"
  region     = var.region
  size       = "s-1vcpu-1gb"
  ssh_keys   = local.ssh_keys
  ipv6       = true
  monitoring = true
  vpc_uuid   = digitalocean_vpc.vpc.id
  user_data  = local.user_data
  tags = [digitalocean_tag.ssh_access.id, digitalocean_tag.full_outbound_access.id,
  digitalocean_tag.internal_web_server.id, digitalocean_tag.postgres_access.id]
  lifecycle {
    create_before_destroy = true
  }
}

resource "digitalocean_droplet" "front_api" {
  image      = var.droplet_image
  name       = "front-api"
  region     = var.region
  size       = "s-1vcpu-1gb"
  ssh_keys   = local.ssh_keys
  ipv6       = true
  monitoring = true
  vpc_uuid   = digitalocean_vpc.vpc.id
  user_data  = local.user_data
  tags = [digitalocean_tag.ssh_access.id, digitalocean_tag.full_outbound_access.id, digitalocean_tag.internal_web_server.id,
  digitalocean_tag.web_server.id, digitalocean_tag.postgres_access.id]
  lifecycle {
    create_before_destroy = true
  }
}

resource "digitalocean_droplet" "socket_server" {
  image      = var.droplet_image
  name       = "socket-server"
  region     = var.region
  size       = "s-1vcpu-1gb"
  ssh_keys   = local.ssh_keys
  ipv6       = true
  monitoring = true
  vpc_uuid   = digitalocean_vpc.vpc.id
  user_data  = local.user_data
  tags       = [digitalocean_tag.ssh_access.id, digitalocean_tag.full_outbound_access.id, digitalocean_tag.internal_web_server.id,
  digitalocean_tag.web_server.id, digitalocean_tag.postgres_access.id]
  lifecycle {
    create_before_destroy = true
  }
}

resource "digitalocean_droplet" "processor" {
  image      = var.droplet_image
  name       = "processor"
  region     = var.region
  size       = "s-1vcpu-1gb"
  ssh_keys   = local.ssh_keys
  ipv6       = true
  monitoring = true
  vpc_uuid   = digitalocean_vpc.vpc.id
  user_data  = local.user_data
  tags       = [digitalocean_tag.ssh_access.id, digitalocean_tag.full_outbound_access.id,
  digitalocean_tag.internal_web_server.id, digitalocean_tag.postgres_access.id]
  lifecycle {
    create_before_destroy = true
  }
}

resource "digitalocean_droplet" "turn_server_1" {
  image      = var.droplet_image
  name       = "turn-server-1"
  region     = var.region
  size       = "s-1vcpu-1gb"
  ssh_keys   = local.ssh_keys
  ipv6       = true
  monitoring = true
  vpc_uuid   = digitalocean_vpc.vpc.id
  user_data  = local.user_data
  tags       = [digitalocean_tag.ssh_access.id, digitalocean_tag.full_outbound_access.id,
  digitalocean_tag.internal_web_server.id, digitalocean_tag.turn_server.id]
  lifecycle {
    create_before_destroy = true
  }
}

resource "digitalocean_reserved_ip_assignment" "front_api" {
  ip_address = digitalocean_reserved_ip.front_api.ip_address
  droplet_id = digitalocean_droplet.front_api.id
}

resource "digitalocean_reserved_ip_assignment" "socket_server" {
  ip_address = digitalocean_reserved_ip.socket_server.ip_address
  droplet_id = digitalocean_droplet.socket_server.id
}