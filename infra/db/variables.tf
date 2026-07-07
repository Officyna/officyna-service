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

variable "vpc_id" {
  description = "ID da VPC existente"
  type        = string
  default     = "vpc-067121acce6d97a88"
}

variable "subnet_ids" {
  description = "Lista de IDs das Subnets"
  type        = list(string)
  default     = ["subnet-022497a11c68c031c", "subnet-0ecc05f3a98453f09"]
}