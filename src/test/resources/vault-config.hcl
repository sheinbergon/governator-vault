backend "file" {
  path = "vault-data"
}

listener "tcp" {
  address = "127.0.0.1:8222"
  tls_disable = 1
}

default_lease_ttl = "999h"
max_lease_ttl = "999h"