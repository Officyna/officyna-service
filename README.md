# 🛠️ Officyna - Sistema Integrado de Gestão de Oficina Mecânica
Este projeto representa o MVP (Produto Mínimo Viável) desenvolvido para o Tech Challenge da Fase 1. A solução visa digitalizar a jornada de atendimento de uma oficina mecânica, substituindo processos manuais por um fluxo automatizado e seguro.

## 📋 Sumário

* [Objetivo do Projeto](#objetivo-do-projeto)
* [Funcionalidades Implementadas](#funcionalidades-implementadas)
* [Arquitetura Técnica](#arquitetura-técnica)
* [Segurança e Qualidade](#segurança-e-qualidade)
* [Instruções de Execução](#instruções-de-execução)

# 🎯 Objetivo do Projeto
O projeto `Officyna` é um sistema integrado de gestão para oficinas mecânicas de automóveis que visa:
* Digitalizar o atendimento;
* Substitur processos manuais por fluxos automatizados e seguros;

Nesta segunda fase, os objetivos principais são modernizar a infraestrutura e a arquitetura do software, garantindo que o sistema seja escalável, resiliente e siga as melhores práticas de mercado
Para isso, o foco está na implementação da Clean Architecture para organizar a lógica de negócio, na Dockerização para empacotamento em containers, e no uso de Kubernetes para orquestração em nuvem
Além disso, a fase foca na automação total via Infraestrutura como Código (IaC) com Terraform e pipelines de CI/CD para garantir agilidade e confiabilidade na entrega do software

## ✨ Funcionalidades Implementadas
### 1. Gestão de Ordens de Serviço (OS)
   Abertura de OS: Identificação por CPF/CNPJ e cadastro detalhado do veículo.

- Orçamento Automático: Cálculo automático de valores baseado em serviços (labors) e peças (supplies) adicionados.

- Status da OS: Ciclo de vida completo com os status: Recebida, Em diagnóstico, Aguardando aprovação, Em execução, Finalizada e Entregue.

- Acompanhamento via API: Endpoint dedicado para consulta do cliente através do documento (CPF/CNPJ).

### 2. Gestão Administrativa (CRUDs)
   O sistema provê interfaces REST para o gerenciamento de:

- Clientes: Cadastro completo com validação de documentos.

- Veículos: Vínculo com clientes e validação de placas.

- Serviços: Listagem de mão de obra disponível.

- Peças e Insumos: Controle de inventário com alerta de estoque baixo.

### 3. Monitoramento
   Tempo Médio de Execução: Serviço especializado que calcula e monitora a performance da oficina por tipo de serviço.

# 🏗️ Arquitetura Técnica
![img_2.png](img_2.png)

## Componentes da Aplicação
A aplicação é construída como um back-end monolítico organizado seguindo os princípios da Clean Architecture, dividindo-se em camadas de API, Domínio (DDD) e Infraestrutura .

```bash
officyna-service/
├── .github/
│   └── workflows/
│       └── main.yml                  # Pipeline de CI/CD
├── db-seed/
│   └── 01-seed.js                    # Scripts de inicialização do banco
├── infra/                            # Provisionamento IaC
│   ├── main.tf                       # AWS DocumentDB
│   ├── outputs.tf
│   ├── providers.tf
│   ├── README.md
│   ├── variables.tf
│   └── versions.tf
├── k8s/                              # Manifestos Kubernetes
│   ├── configmap.yaml
│   ├── deployment.yaml
│   ├── hpa.yaml
│   └── service.yaml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/officyna/
│   │   │       ├── administrative/   # Gestão de Clientes, Veículos, Usuários, etc.
│   │   │       │   ├── customer/
│   │   │       │   │   ├── api/      # Controllers e DTOs
│   │   │       │   │   └── domain/   # Entidades, Services e Interfaces
│   │   │       │   ├── labor/
│   │   │       │   ├── supply/
│   │   │       │   ├── user/
│   │   │       │   └── vehicle/
│   │   │       ├── infrastructure/   # Camada Técnica / Cross-cutting
│   │   │       │   ├── auth/         # Login e Autenticação
│   │   │       │   ├── config/       # Spring, Mongo, Security, Swagger
│   │   │       │   ├── exception/    # Tratamento Global de Erros
│   │   │       │   ├── persistence/  # Implementação de Repositórios
│   │   │       │   │   ├── mapper/   # Conversão Entity <-> Document
│   │   │       │   │   └── mongodb/  # Gateways e Repositórios Spring Data
│   │   │       │   └── security/     # JWT e UserDetails
│   │   │       ├── monitoring/       # Acompanhamento de Performance
│   │   │       │   ├── api/
│   │   │       │   └── domain/
│   │   │       ├── serviceorder/     # Coração do Sistema - Ordens de Serviço
│   │   │       │   ├── api/
│   │   │       │   └── domain/       # DTOs, Entidades e Regras de Negócio
│   │   │       └── ApplicationService.java # Classe Principal
│   │   └── resources/
│   │       └── application.yml
│   └── test/                         # Estrutura de testes espelhada da aplicação
├── docker-compose.yml
├── Dockerfile
├── mvnw
├── mvnw.cmd
├── pom.xml                           # Gerenciador de Dependências Maven
└── README.md
````

### Camada de Domínio: 
Contém as entidades puras (como Customer e ServiceOrder) e as interfaces de repositório, isolando as regras de negócio de detalhes técnicos.

### Camada de Aplicação/Casos de Uso: 
Implementa as funcionalidades do software, como a abertura e o cálculo de orçamentos de ordens de serviço.

### Camada de Adaptadores (Interface Adapters): 
Inclui os controllers REST, DTOs e Gateways que traduzem dados entre o mundo exterior e o núcleo da aplicação.

### Camada de Infraestrutura: 
Lida com frameworks e drivers, como as configurações do Spring Boot e a persistência real no MongoDB.

### Infraestrutura Provisionada
A infraestrutura é provisionada na AWS de forma automatizada, consistindo em:
* **Rede (VPC):** Uma Virtual Private Cloud isolada com subnets públicas para acesso externo e subnets privadas para segurança do banco de dados.
* **Orquestração (Amazon EKS):** Um cluster Kubernetes gerenciado que executa os nós de trabalho (worker nodes) onde a aplicação é implantada.
* **Banco de Dados (Amazon DocumentDB/MongoDB):** Um cluster NoSQL compatível com MongoDB, configurado para alta disponibilidade e persistência.
* **Persistência:** Uso de volumes EBS (Elastic Block Store) via Persistent Volume Claims (PVC) para garantir que os dados das ordens de serviço sobrevivam a reinicializações de containers.

## Fluxo de Deploy
O fluxo de implantação é totalmente automatizado via GitHub Actions:
* **Integração Contínua (CI):** Ao realizar um push, o pipeline executa o checkout do código, build com Maven e testes automatizados para garantir a qualidade.
* **Dockerização:** Após os testes, uma nova imagem Docker é gerada e enviada para o GitHub Container Registry (GHCR).
* **Provisionamento de Infraestrutura:** O pipeline utiliza o Terraform para aplicar as mudanças na infraestrutura da AWS (VPC, EKS, DB).
* **Entrega Contínua (CD):** Por fim, os manifestos Kubernetes são aplicados no cluster EKS para atualizar a aplicação sem interrupção (Rolling Update).

## 🛡️ Segurança e Qualidade
- Autenticação JWT: Implementada para proteger todos os endpoints administrativos.

- Validação de Dados: Classes utilitárias para validação rigorosa de CPF, CNPJ (Modulo 11) e placas de veículos.

- Testes Automatizados: O projeto exige cobertura mínima de 80% nos domínios críticos (OS, Orçamentos, Estoque e Segurança).

# 🚀 Instruções de Execução
## local
O projeto está configurado para uma execução local simples via Docker.
- Build da Aplicação: O Dockerfile deve ser utilizado para gerar a imagem da aplicação.
- Orquestração: O arquivo docker-compose.yml sobe a aplicação e a instância do MongoDB.
- Comando de inicialização:

````bash
docker-compose up -d --build
````

## Acesso à Documentação
* Após subir o ambiente local, acesse o Swagger em: http://localhost:8080/swagger-ui.html
* Após realziar o deploy no kubernets: <https://{dns-elb}.us-east-1.elb.amazonaws.com/swagger-ui/index.html>

## Deploy em Kubernetes
![img_1.png](img_1.png)
A implantação no cluster utiliza manifestos YAML localizados na pasta `/k8s`:
* **Aplicação:** Implante os pods e a estratégia de replicação com kubectl 
````bash 
kubectl apply -f deployment.yaml
````
* **Configurações:** Aplique as variáveis de ambiente e segredos com 
````bash 
kubectl apply -f configmap.yaml
````
* **Exposição:** Exponha a API internamente ou via Load Balancer com 
````bash 
kubectl apply -f service.yaml
````
* **Escalabilidade:** Ative o escalonamento automático baseado em uso de CPU/Memória com
````bash 
kubectl apply -f hpa.yaml
````

## Provisionamento da Infraestrutura com Terraform
Para provisionar os recursos na AWS, utilize os arquivos na pasta `infra/terraform/`:
- **Inicialização:** Execute para baixar os providers da AWS e configurar o backend remoto (S3).
````bash 
terraform init
````
- **Validação:** Verifique a sintaxe com 
````bash 
terraform validate
````
- **Planejamento:** Visualize os recursos que serão criados com
````bash 
terraform plan
````
- **Aplicação:** Provisione a rede, o banco e o cluster EKS com
````bash 
terraform apply -auto-approve
````
- **Configuração de Acesso:** Após o provisionamento, utilize o comando (usando os dados do outputs.tf) para conectar seu kubectl ao cluster na nuvem.
````bash
aws eks update-kubeconfig
````