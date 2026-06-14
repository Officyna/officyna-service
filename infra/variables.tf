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