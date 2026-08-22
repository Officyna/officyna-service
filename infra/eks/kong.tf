# Terraform-managed Kubernetes resources to deploy Kong in DB-less (declarative) mode

resource "kubernetes_namespace" "officyna" {
  metadata {
    name = "officyna"
    labels = {
      "app.kubernetes.io/name" = "officyna"
    }
  }
}

resource "kubernetes_config_map" "kong_declarative_config" {
  metadata {
    name      = "kong-declarative-config"
    namespace = kubernetes_namespace.officyna.metadata[0].name
    labels = {
      "app.kubernetes.io/name" = "kong"
    }
  }

  data = {
    "kong.yml" = <<-KONGYML
_format_version: "2.1"
services:
  - name: officyna-service
    url: http://officyna-service:80
    routes:
      - name: officyna-route
        paths:
          - "/"
plugins: []
KONGYML
  }
}

resource "kubernetes_deployment" "kong" {
  metadata {
    name      = "kong"
    namespace = kubernetes_namespace.officyna.metadata[0].name
    labels = {
      "app.kubernetes.io/name" = "kong"
    }
  }

  spec {
    replicas = var.kong_replicas

    selector {
      match_labels = {
        "app.kubernetes.io/name" = "kong"
      }
    }

    template {
      metadata {
        labels = {
          "app.kubernetes.io/name" = "kong"
        }
      }

      spec {
        container {
          name  = "kong"
          image = var.kong_image

          env {
            name  = "KONG_DATABASE"
            value = "off"
          }

          env {
            name  = "KONG_DECLARATIVE_CONFIG"
            value = "/kong/declarative/kong.yml"
          }

          env {
            name  = "KONG_PROXY_ACCESS_LOG"
            value = "/dev/stdout"
          }

          env {
            name  = "KONG_ADMIN_ACCESS_LOG"
            value = "/dev/stdout"
          }

          env {
            name  = "KONG_PROXY_ERROR_LOG"
            value = "/dev/stderr"
          }

          env {
            name  = "KONG_ADMIN_ERROR_LOG"
            value = "/dev/stderr"
          }

          env {
            name  = "KONG_ADMIN_LISTEN"
            value = "0.0.0.0:8001, 0.0.0.0:8444 ssl"
          }

          env {
            name  = "KONG_ADMIN_GUI_URL"
            value = "http://localhost:8002"
          }

          port {
            container_port = 8000
          }
          port {
            container_port = 8001
          }
          port {
            container_port = 8002
          }
          port {
            container_port = 8444
          }

          volume_mount {
            name       = "kong-declarative"
            mount_path = "/kong/declarative/kong.yml"
            sub_path   = "kong.yml"
            read_only  = true
          }

          resources {
            limits = {
              cpu    = "1000m"
              memory = "1Gi"
            }
            requests = {
              cpu    = "250m"
              memory = "512Mi"
            }
          }
        }

        volume {
          name = "kong-declarative"

          config_map {
            name = kubernetes_config_map.kong_declarative_config.metadata[0].name

            item {
              key  = "kong.yml"
              path = "kong.yml"
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "kong_proxy" {
  metadata {
    name      = "kong-proxy"
    namespace = kubernetes_namespace.officyna.metadata[0].name
    labels = {
      "app.kubernetes.io/name" = "kong"
    }
  }

  spec {
    selector = {
      "app.kubernetes.io/name" = "kong"
    }

    port {
      name        = "proxy-http"
      port        = 8000
      target_port = 8000
      protocol    = "TCP"
    }

    port {
      name        = "proxy-ssl"
      port        = 8443
      target_port = 8444
      protocol    = "TCP"
    }

    type = var.kong_service_type
  }
}

resource "kubernetes_service" "kong_admin" {
  metadata {
    name      = "kong-admin"
    namespace = kubernetes_namespace.officyna.metadata[0].name
    labels = {
      "app.kubernetes.io/name" = "kong"
    }
  }

  spec {
    selector = {
      "app.kubernetes.io/name" = "kong"
    }

    port {
      name        = "admin-http"
      port        = 8001
      target_port = 8001
      protocol    = "TCP"
    }

    port {
      name        = "admin-gui"
      port        = 8002
      target_port = 8002
      protocol    = "TCP"
    }

    # By default keep admin API as ClusterIP unless explicitly requested
    type = var.kong_admin_expose ? var.kong_service_type : "ClusterIP"
  }
}