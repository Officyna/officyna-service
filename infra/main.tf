resource "kind_cluster" "officyna" {
  name           = var.cluster_name
  node_image     = var.node_image
  wait_for_ready = true

  kind_config {
    kind        = "Cluster"
    api_version = "kind.x-k8s.io/v1alpha4"

    node {
      role = "control-plane"

      extra_port_mappings {
        container_port = 30080
        host_port      = var.host_http_port
      }
    }

    node {
      role = "worker"
    }
  }
}

provider "helm" {
  kubernetes {
    host                   = kind_cluster.officyna.endpoint
    client_certificate     = kind_cluster.officyna.client_certificate
    client_key             = kind_cluster.officyna.client_key
    cluster_ca_certificate = kind_cluster.officyna.cluster_ca_certificate
  }
}

provider "kubernetes" {
  host                   = kind_cluster.officyna.endpoint
  client_certificate     = kind_cluster.officyna.client_certificate
  client_key             = kind_cluster.officyna.client_key
  cluster_ca_certificate = kind_cluster.officyna.cluster_ca_certificate
}

resource "helm_release" "metrics_server" {
  name             = "metrics-server"
  repository       = "https://kubernetes-sigs.github.io/metrics-server/"
  chart            = "metrics-server"
  namespace        = "kube-system"
  version          = "3.12.1"
  create_namespace = false

  set {
    name  = "args[0]"
    value = "--kubelet-insecure-tls"
  }

  depends_on = [kind_cluster.officyna]
}

resource "kubernetes_namespace" "officyna" {
  metadata {
    name = "officyna"
    labels = {
      "app.kubernetes.io/name" = "officyna"
    }
  }

  depends_on = [kind_cluster.officyna]
}