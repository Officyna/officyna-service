# 🛠️ Officyna - Sistema Integrado de Gestão de Oficina Mecânica

> **Tech Challenge - Fase 3: Operação Corporativa, Serverless e Escalabilidade Cloud**

Este projeto representa a evolução da solução desenvolvida para o Tech Challenge. Na Fase 3, o sistema passa a operar em nível corporativo, com foco em segurança, alta disponibilidade, observabilidade e escalabilidade serverless em ambiente de nuvem.

---

## 📋 Sumário
* [Objetivo do Projeto](#-objetivo-do-projeto)
* [Funcionalidades Implementadas](#-funcionalidades-implementadas)
* [Estrutura de Repositórios e CI/CD](#-estrutura-de-repositorios-e-ci/cd)
* [Arquitetura Técnica & Segurança](#-arquitetura-técnica-&-segurança)
* [Documentação Arquitetural](#-documentação-arquitetural)
* [Monitoramento e Observabilidade](#-monitoramento-e-observabilidade)
* [Instruções de Execução e Deploy](#-instruções-de-execução-e-deploy)


---

## 🎯 Objetivo do Projeto
O projeto **Officyna** é um sistema integrado de gestão para oficinas mecânicas de automóveis que visa digitalizar o atendimento e substituir processos manuais por fluxos automatizados e seguros.

Na **Fase 3**, os objetivos centrais são:
* **Escalabilidade e Segurança em Nuvem:** Garantir a expansão da oficina para múltiplas unidades e alta concorrência de clientes.
* **Autenticação Serverless e API Gateway:** Proteger rotas sensíveis da aplicação utilizando AWS API Gateway e Function Serverless (AWS Lambda) para validação de CPF e emissão de tokens JWT.
* **Banco de Dados Gerenciado:** Transição e melhoria da modelagem relacional em banco de dados gerenciado em nuvem (ex: PostgreSQL / MySQL).
* **Segregação de Repositórios:** Divisão da aplicação em 4 repositórios independentes com governança rigorosa de código e pipelines CI/CD automatizados.
* **Observabilidade Corporativa:** Implementação de monitoramento em tempo real com Datadog/New Relic, métricas de K8s, logs estruturados em JSON e dashboards operacionais.

---

## ✨ Funcionalidades Implementadas

### 1. Autenticação e Autorização Serverless
* **Validação de CPF:** Function Serverless que valida o CPF do cliente e consulta seu status na base de dados.
* **Geração de JWT:** Emissão de tokens JWT válidos para consumo seguro das APIs protegidas através do API Gateway.
* **Proteção de Endpoints:** Proteção de rotas sensíveis com autorizador customizado no API Gateway.

### 2. Gestão de Ordens de Serviço (OS)
* **Abertura de OS:** Identificação por CPF/CNPJ e cadastro detalhado do veículo.
* **Orçamento Automático:** Cálculo automático de valores baseado em mão de obra (*labors*) e peças (*supplies*).
* **Ciclo de Vida de Status:** Recebida, Em diagnóstico, Aguardando aprovação, Em execução, Finalizada e Entregue.
* **Consulta via API:** Endpoint público/protegido para acompanhamento do status da OS pelo cliente.

### 3. Telemetria e Métricas Operacionais
* **Monitoramento de SLA:** Cálculo de tempo médio de execução por status (Diagnóstico, Execução e Finalização).
* **Acompanhamento de OS:** Dashboard com volume diário de ordens e taxas de erro em integrações.

---

## 🗂️ Estrutura de Repositórios e CI/CD

Seguindo as diretrizes da Fase 3, o projeto é segregado em **4 repositórios separados**, cada um com seu pipeline de CI/CD automatizado via **GitHub Actions** (ou GitLab CI) com deploy automático em nuvem:

1. `officyna-lambda`: Código da Function Serverless para validação de CPF e geração de token JWT.
2. `officyna-infra-k8s`: Código Terraform para provisionamento do Cluster Kubernetes (EKS).
3. `officyna-infra-db`: Código Terraform para provisionamento e configuração do Banco de Dados Gerenciado.
4. `officyna-service`: Aplicação principal (microsserviços/back-end) executada no cluster Kubernetes e API Gateway.

### Regras de Governança de Código:
* **Branches `main` protegidas:** Commits diretos são bloqueados.
* **Pull Requests (PR):** Merge obrigatório via PR com aprovação e execução de checks de CI.
* **Deploy Automatizado:** CD configurado para ambientes de Homologação e Produção.

---

## 🏗️ Arquitetura Técnica & Segurança

### Componentes de Infraestrutura na Nuvem
* **API Gateway (Kong):** Ponto de entrada único para roteamento de requisições e integração com o autorizador serverless.
* **Function Serverless (AWS Lambda):** Função executada sob demanda para autenticação via CPF e geração de JWT.
* **Cluster Kubernetes (Amazon EKS):** Orquestração dos pods da aplicação com suporte a escalabilidade automática (HPA).
* **Banco de Dados Gerenciado (DocumentDB):** Persistência relacional gerenciada, garantindo alta disponibilidade, backup automatizado e performance.
* **Terraform:** Provisionamento declarativo de toda a infraestrutura (IaC).

### Estrutura do Código da Aplicação (`officyna-service`)
A aplicação principal segue a **Clean Architecture** e princípios de Domain-Driven Design (DDD):

```bash
officyna-service/
├── .github/
│   └── workflows/          # Pipelines de CI/CD (Build, Test, Push Docker, Deploy K8s)
├── db-seed/                # Scripts de migração e dados iniciais
├── infra/                  # Módulos Terraform de suporte
├── k8s/                    # Manifestos de Kubernetes (Deployment, Service, HPA, ConfigMap)
└── src/
    ├── main/
    │   ├── java/
    │   │   └── br/com/officyna/
    │   │       ├── administrative/ # Agregados: Customer, Vehicle, Labor, Supply, User
    │   │       ├── serviceorder/   # Módulo de Ordem de Serviço (Ciclo de vida e regras de negócio)
    │   │       ├── inventory/      # Módulo de Controle de Estoque
    │   │       ├── monitoring/     # Módulo de Coleta de Métricas
    │   │       └── infrastructure/ # Adaptadores de Entrada/Saída, DB, Segurança e Configurações
    └── test/               # Cobertura de testes unitários, de integração e arquitetura (> 80%)
```

### 🛡️ Segurança e Qualidade
* **Autenticação JWT Serverless:** Rotas sensíveis protegidas com validação prévia de token assinado.
* **Validação Rigorosa:** Modulo 11 para CPF/CNPJ e validação de formatos de placa.
* **Qualidade de Código:** Cobertura de testes automatizados superior a 80% nos módulos de domínio.

 ```json
{
  "sub": "admin@officyna.com",
  "roles": "ADMIN",
  "iat": 1789173737,
  "exp": 1789260137
}
```

---

## 📐 Documentação Arquitetural

A documentação detalhada da arquitetura inclui:

1. **Diagrama de Componentes:** Mapeamento da visão de nuvem (API Gateway, Lambda Auth, Kubernetes EKS, RDS/Banco Gerenciado e Observabilidade).
2. **Diagrama de Sequência:** Fluxo detalhado passo a passo da Autenticação via CPF (Lambda -> DB -> JWT) e da Abertura/Processamento de Ordem de Serviço.
3. **RFCs (Request for Comments):**
   * *RFC-001:* Escolha da Nuvem (AWS) e Estratégia de Provisionamento com Terraform.
   * *RFC-002:* Escolha do Banco de Dados Gerenciado Relacional.
   * *RFC-003:* Arquitetura Serverless para Autenticação via CPF.
4. **ADRs (Architecture Decision Records):**
   * *ADR-001:* Utilização de API Gateway integrado a Lambda Authorizer.
   * *ADR-002:* Adopção de Horizontal Pod Autoscaler (HPA) baseado em CPU/Memória.
5. **Modelagem de Banco de Dados:**
   * Justificativa formal da escolha do Banco Relacional e ajustes de normalização.
   * Diagramas Entidade-Relacionamento (ER) com especificação de chaves e relacionamentos.

---

## 📊 Monitoramento e Observabilidade

Integração nativa com plataformas corporativas de observabilidade (**New Relic**):

### Métricas e Telemetria:
* **APIs:** Latência de requisições, taxa de erro e throughput.
* **Kubernetes:** Consumo de CPU, memória, estado dos Pods, healthchecks e uptime.
* **Alertas:** Notificações automáticas em tempo real para falhas no processamento de ordens de serviço.
* **Logs Estruturados:** Emissão de logs em formato JSON contendo `correlation-id` para rastreabilidade distribuída de requisições.

### Dashboards Operacionais Expostos:
* **Volume Diário de OS:** Total de ordens de serviço criadas por dia.
* **Tempo Médio de Execução por Status:** Acompanhamento dos tempos em Diagnóstico, Execução e Finalização.
* **Integrabilidade & Falhas:** Painel de monitoramento de erros em integrações e serviços externos.

---

## 🚀 Instruções de Execução e Deploy

### 1. Execução Local (Ambiente de Desenvolvimento)
```bash
# Executar a aplicação e banco de dados localmente via Docker Compose
docker-compose up -d --build
```

### 2. Provisionamento com Terraform (IaC)
```bash
# Navegar até a pasta do repositório de infraestrutura
cd infra/terraform

terraform init
terraform validate
terraform plan
terraform apply -auto-approve

# Configurar o acesso ao cluster Kubernetes
aws eks update-kubeconfig --name officyna-cluster --region us-east-1
```

### 3. Implantação no Kubernetes
```bash
# Aplicar manifestos no cluster EKS
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/hpa.yaml
```

### 4. Acesso à Documentação da API
* **Swagger UI Local:** `http://localhost:8080/swagger-ui.html`
* **Swagger UI no K8s:** `https://{dns-elb}.us-east-1.elb.amazonaws.com/swagger-ui/index.html`

---

## 🔗 Entregáveis e Links do Projeto

### Repositórios Git (Organização em 4 Repositórios):
* [Repositório 1 - Lambda Serverless (Auth)](https://github.com/Officyna/officyna-lambda)
* [Repositório 2 - Infraestrutura Kubernetes (Terraform)](https://github.com/Officyna/officyna-infra-k8s)
* [Repositório 3 - Infraestrutura Banco Gerenciado (Terraform)](https://github.com/Officyna/officyna-infra-db)
* [Repositório 4 - Aplicação Principal](https://github.com/Officyna/officyna-service)

> **Permissão:** Confirmado o acesso do usuário `soat-architecture` como colaborador em todos os 4 repositórios.

### Vídeo de Demonstração (YouTube / Vimeo):
* 🎥 **Link do Vídeo (máx 15 min):** [Assistir Demonstração da Fase 3 no YouTube](https://youtube.com)
* **Conteúdo demonstrado no vídeo:**
  1. Autenticação com CPF via Function Serverless.
  2. Execução da pipeline de CI/CD e deploy automatizado.
  3. Consumo das APIs protegidas via API Gateway.
  4. Dashboard de monitoramento com análise em tempo real (Datadog/New Relic).
  5. Rastreamento de logs estruturados e traces de requisições.
