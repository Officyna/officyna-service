output "cluster_name" {
  description = "Nome do cluster kind criado"
  value       = kind_cluster.officyna.name
}

output "kubeconfig_path" {
  description = "Caminho do kubeconfig gerado pelo kind"
  value       = kind_cluster.officyna.kubeconfig_path
}

output "endpoint" {
  description = "Endpoint da API do Kubernetes"
  value       = kind_cluster.officyna.endpoint
}

output "namespace" {
  description = "Namespace da aplicação"
  value       = kubernetes_namespace.officyna.metadata[0].name
}