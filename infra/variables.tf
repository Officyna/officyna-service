variable "cluster_name" {
  description = "Nome do cluster Kubernetes (kind)"
  type        = string
  default     = "officyna"
}

variable "node_image" {
  description = "Imagem do nó kind (versão do Kubernetes)"
  type        = string
  default     = "kindest/node:v1.30.0"
}

variable "host_http_port" {
  description = "Porta do host mapeada para o NodePort de ingresso (acesso local à API)"
  type        = number
  default     = 30080
}

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