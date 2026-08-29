variable "aws_region" {
  description = "Região da AWS"
  type        = string
  default     = "us-east-1"
}

variable "db_password" {
  description = "Senha do banco de dados DocumentDB"
  type        = string
  sensitive   = true
}

module "db" {
  source      = "./db"
  db_password = var.db_password
}

module "eks" {
  source     = "./eks"
  vpc_id     = module.db.vpc_id
  subnet_ids = module.db.subnet_ids
}

output "docdb_endpoint" {
  description = "Endpoint do DocumentDB"
  value       = module.db.docdb_endpoint
}

output "vpc_id" {
  description = "ID da VPC"
  value       = module.db.vpc_id
}

output "subnet_ids" {
  description = "Subnets da VPC"
  value       = module.db.subnet_ids
}