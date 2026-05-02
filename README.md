# 🛠️ Officyna - Sistema Integrado de Gestão de Oficina Mecânica
Este projeto representa o MVP (Produto Mínimo Viável) desenvolvido para o Tech Challenge da Fase 1. A solução visa digitalizar a jornada de atendimento de uma oficina mecânica, substituindo processos manuais por um fluxo automatizado e seguro.

## 📋 Sumário

* [Objetivo do Projeto](#objetivo-do-projeto)

* [Funcionalidades Implementadas](#funcionalidades-implementadas)

* [Arquitetura Técnica](#arquitetura-técnica)

* [Segurança e Qualidade](#segurança-e-qualidade)

* [Instruções de Execução](#instruções-de-execução)

# 🎯 Objetivo do Projeto
Resolver problemas de desorganização, erros de priorização e falhas no controle de estoque através de um Sistema Integrado de Atendimento e Execução de Serviços. O foco principal é a gestão de ordens de serviço, clientes e peças, utilizando os princípios de Domain-Driven Design (DDD).

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

## 🏗️ Arquitetura Técnica
- Padrão: Back-end monolítico organizado em camadas (API, Domain, Infrastructure).

- Banco de Dados: MongoDB (NoSQL), escolhido pela flexibilidade no armazenamento de documentos de OS e históricos de manutenção.

- Documentação: APIs documentadas com Swagger/OpenAPI para facilitar a integração e testes.

## 🛡️ Segurança e Qualidade
- Autenticação JWT: Implementada para proteger todos os endpoints administrativos.

- Validação de Dados: Classes utilitárias para validação rigorosa de CPF, CNPJ (Modulo 11) e placas de veículos.

- Testes Automatizados: O projeto exige cobertura mínima de 80% nos domínios críticos (OS, Orçamentos, Estoque e Segurança).

## 🚀 Instruções de Execução
O projeto está configurado para uma execução local simples via Docker.

- Build da Aplicação: O Dockerfile deve ser utilizado para gerar a imagem da aplicação.

- Orquestração: O arquivo docker-compose.yml sobe a aplicação e a instância do MongoDB.

- Comando de inicialização:

````bash
docker-compose up -d --build
````
- Acesso à Documentação: Após subir o ambiente, acesse o Swagger em: http://localhost:8080/swagger-ui.html