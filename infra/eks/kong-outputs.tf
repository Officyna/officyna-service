output "kong_proxy_endpoint" {
  description = "External IP/hostname of Kong proxy (if LoadBalancer) or 'pending'"
  value = try(
    kubernetes_service.kong_proxy.status[0].load_balancer[0].ingress[0].ip,
    kubernetes_service.kong_proxy.status[0].load_balancer[0].ingress[0].hostname,
    "pending"
  )
}

output "kong_admin_endpoint" {
  description = "External IP/hostname of Kong admin (if exposed) or 'clusterip'"
  value = try(
    kubernetes_service.kong_admin.status[0].load_balancer[0].ingress[0].ip,
    kubernetes_service.kong_admin.status[0].load_balancer[0].ingress[0].hostname,
    kubernetes_service.kong_admin.spec[0].cluster_ip,
    "pending"
  )
}