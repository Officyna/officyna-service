variable "aws_region" {
  description = "Região da AWS"
  type        = string
  default     = "us-east-1"
}
variable "project_name" {
  description = "Nome do projeto"
  type        = string
  default = "officyna-service"
}

variable "cidr_vpc" {
  description = "CIDR da VPC"
  type        = string
  default = "10.0.0.0/16"
}

variable "tags" {
  default = {
    Name = "officyna-service"
  }
}

variable "instance_type" {
  description = "Tamanho e família padrão da instância EC2 do node"
  type        = string
  default = "t3.medium"
}

variable "vpc_id" {
  description = "ID da VPC utilizada pelo DocumentDB"
  type        = string
}