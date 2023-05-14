resource "digitalocean_vpc" "vpc" {
  name   = "system-template-vpc"
  region = var.region
}

resource "digitalocean_tag" "ssh_access" {
  name = "ssh-access"
}

resource "digitalocean_tag" "full_outbound_access" {
  name = "full-outbound-access"
}

resource "digitalocean_tag" "web_server" {
  name = "web-server"
}

resource "digitalocean_tag" "internal_web_server" {
  name = "internal-web-server"
}

resource "digitalocean_tag" "turn_server" {
  name = "turn-server"
}

resource "digitalocean_firewall" "ssh_access" {
  name = "ssh-access-firewall"
  tags = [digitalocean_tag.ssh_access.id]

  inbound_rule {
    protocol         = "tcp"
    port_range       = "22"
    source_addresses = local.all_ip_adresses
  }
}

resource "digitalocean_firewall" "full_outbound_access" {
  name = "full-outbound-access-firewall"
  tags = [digitalocean_tag.full_outbound_access.id]

  outbound_rule {
    protocol              = "udp"
    port_range            = "1-65535"
    destination_addresses = local.all_ip_adresses
  }

  outbound_rule {
    protocol              = "tcp"
    port_range            = "1-65535"
    destination_addresses = local.all_ip_adresses
  }

  outbound_rule {
    protocol              = "icmp"
    destination_addresses = local.all_ip_adresses
  }
}

resource "digitalocean_firewall" "web_server" {
  name = "web-server-firewall"
  tags = [digitalocean_tag.web_server.id]

  inbound_rule {
    protocol         = "tcp"
    port_range       = "80"
    source_addresses = local.all_ip_adresses
  }

  inbound_rule {
    protocol         = "tcp"
    port_range       = "443"
    source_addresses = local.all_ip_adresses
  }

  inbound_rule {
    protocol         = "icmp"
    source_addresses = local.all_ip_adresses
  }
}

resource "digitalocean_firewall" "internal_web_server" {
  name = "internal-web-server-firewall"
  tags = [digitalocean_tag.internal_web_server.id]

  inbound_rule {
    protocol         = "tcp"
    port_range       = "80"
    source_addresses = [digitalocean_vpc.vpc.ip_range]
  }

  inbound_rule {
    protocol         = "tcp"
    port_range       = "443"
    source_addresses = [digitalocean_vpc.vpc.ip_range]
  }

  # Almost all tcp ports open for internal traffic
  inbound_rule {
    protocol         = "tcp"
    port_range       = "8080-49152"
    source_addresses = [digitalocean_vpc.vpc.ip_range]
  }

  inbound_rule {
    protocol         = "icmp"
    source_addresses = [digitalocean_vpc.vpc.ip_range]
  }
}

resource "digitalocean_firewall" "turn_server" {
  name = "turn-server-firewall"
  tags = [digitalocean_tag.turn_server.id]

  inbound_rule {
    protocol         = "icmp"
    source_addresses = [digitalocean_vpc.vpc.ip_range]
  }

  inbound_rule {
    protocol         = "tcp"
    port_range       = "3478"
    source_addresses = local.all_ip_adresses
  }

  inbound_rule {
    protocol         = "udp"
    port_range       = "3478"
    source_addresses = local.all_ip_adresses
  }

  inbound_rule {
    protocol         = "tcp"
    port_range       = "5349"
    source_addresses = local.all_ip_adresses
  }

  inbound_rule {
    protocol         = "udp"
    port_range       = "5349"
    source_addresses = local.all_ip_adresses
  }

  inbound_rule {
    protocol         = "tcp"
    port_range       = "49152-65535"
    source_addresses = local.all_ip_adresses
  }

  inbound_rule {
    protocol         = "udp"
    port_range       = "49152-65535"
    source_addresses = local.all_ip_adresses
  }
}