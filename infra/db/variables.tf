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
  default     = "vpc-0478df41221fb98ed"
}

variable "subnet_ids" {
  description = "Lista de IDs das Subnets"
  type        = list(string)
  default     = ["subnet-02ef5d7c2b8d32609", "subnet-0f7ce643398f86fa4"]
}