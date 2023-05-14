resource "digitalocean_tag" "postgres_access" {
  name = "postgres-access"
}

resource "digitalocean_database_cluster" "postgres" {
  name                 = "system-template-postgres"
  engine               = "pg"
  version              = "14"
  size                 = "db-s-1vcpu-1gb"
  region               = var.region
  node_count           = 1
  private_network_uuid = digitalocean_vpc.vpc.id
}

resource "digitalocean_database_firewall" "postgres" {
  cluster_id = digitalocean_database_cluster.postgres.id
  rule {
    type  = "tag"
    value = digitalocean_tag.postgres_access.id
  }
}
#There is a database, user and password in the output