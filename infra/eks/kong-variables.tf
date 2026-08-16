variable "kong_image" {
  description = "Container image for Kong"
  type        = string
  default     = "kong:latest"
}

variable "kong_replicas" {
  description = "Number of Kong replicas"
  type        = number
  default     = 1
}

variable "kong_admin_expose" {
  description = "Whether to expose Kong Admin API as LoadBalancer (set false for production)"
  type        = bool
  default     = false
}

variable "kong_service_type" {
  description = "Service type for Kong proxy (LoadBalancer/NodePort/ClusterIP)"
  type        = string
  default     = "LoadBalancer"
}