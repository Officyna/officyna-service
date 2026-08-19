variable "aws_region" {
  description = "Região da AWS"
  type        = string
  default     = "us-east-1"
}

variable "db_username" {
  description = "Usuário master do banco de dados"
  type        = string
  default     = "officynasoatdbuser"
}

variable "db_password" {
  description = "Senha do banco de dados (injetada via GitHub Secrets)"
  type        = string
  sensitive   = true
}

variable "cidr_vpc" {
  description = "CIDR da VPC"
  type        = string
  default = "10.0.0.0/16"
}