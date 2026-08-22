module "db" {
  source = "./db"
}

module "eks" {
  source     = "./eks"
  vpc_id     = module.db.vpc_id
  subnet_ids = module.db.subnet_ids
}

output "kong_proxy_endpoint" {
  description = "Endpoint público do Kong proxy"
  value       = module.eks.kong_proxy_endpoint
}

output "kong_admin_endpoint" {
  description = "Endpoint do Kong admin"
  value       = module.eks.kong_admin_endpoint
}