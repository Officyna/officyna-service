# Infraestrutura como Código (Terraform)

Provisiona o cluster Kubernetes local com **kind** (Kubernetes IN Docker) e o
**metrics-server**, pré-requisito do HPA. O banco de dados é o **MongoDB Atlas
(M0, gerenciado)** — fora do cluster — então não há recurso de banco aqui; a
conexão é injetada via Secret do Kubernetes (ver `/k8s` e o pipeline).

## Recursos criados

| Recurso | Descrição |
|---|---|
| `kind_cluster.officyna` | Cluster com 1 control-plane + 1 worker |
| `helm_release.metrics_server` | Coleta CPU/memória para o HPA |
| `kubernetes_namespace.officyna` | Namespace `officyna` da aplicação |

## Pré-requisitos

- [Docker](https://docs.docker.com/get-docker/)
- [Terraform >= 1.5](https://developer.hashicorp.com/terraform/install)
- [kubectl](https://kubernetes.io/docs/tasks/tools/)

## Como aplicar

```bash
cd infra
terraform init
terraform plan
terraform apply -auto-approve

# Aponta o kubectl para o cluster recém-criado
export KUBECONFIG="$(terraform output -raw kubeconfig_path)"
kubectl get nodes
```

## Como destruir

```bash
cd infra
terraform destroy -auto-approve
```

## Variáveis

| Variável | Padrão | Descrição |
|---|---|---|
| `cluster_name` | `officyna` | Nome do cluster kind |
| `node_image` | `kindest/node:v1.30.0` | Versão do Kubernetes |
| `host_http_port` | `30080` | Porta do host para acesso local à API |