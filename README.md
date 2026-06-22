# Invest Settlement Hub

Sistema distribuído de liquidação financeira inspirado em arquiteturas utilizadas por bancos digitais, corretoras e plataformas de investimento.

O projeto demonstra a implementação de uma plataforma orientada a eventos utilizando microsserviços, processamento assíncrono, observabilidade, segurança, infraestrutura como código e integração com serviços AWS.

---

# Visão Geral

O Invest Settlement Hub simula o fluxo completo de processamento de ordens financeiras, desde a criação da ordem até a geração do comprovante de liquidação.

Principais características:

* Arquitetura de Microsserviços
* Event-Driven Architecture
* Processamento Assíncrono
* API Gateway
* JWT Authentication
* AWS SQS
* AWS S3
* LocalStack
* Observabilidade Completa
* Infraestrutura como Código
* CI/CD com GitHub Actions

---

# Arquitetura

```text
Cliente
   │
   ▼
API Gateway
   │
   ▼
Order Service
   │
   ▼
order-created-queue
   │
   ▼
Settlement Service
   │
   ├──────────────► notification-queue
   │                     │
   │                     ▼
   │             Notification Service
   │
   └──────────────► statement-queue
                         │
                         ▼
                 Statement Service
                         │
                         ▼
                       AWS S3

Observabilidade:
- Zipkin
- Prometheus
- Grafana
```

---

# Microsserviços

## Gateway Service

Responsável por:

* Autenticação JWT
* Controle de acesso baseado em roles
* Roteamento centralizado
* Distributed Tracing

### Roles

* CUSTOMER
* PLATFORM_ENGINEER
* ADMIN

### Tecnologias

* Spring Cloud Gateway
* Spring Security
* JWT
* Micrometer
* Zipkin

---

## Order Service

Responsável pelo registro das ordens financeiras.

### Funcionalidades

* Cadastro de ordens
* Consulta de ordens
* Persistência em MySQL
* Publicação de eventos SQS
* Correlation ID
* Logs estruturados

### Tecnologias

* Java 21
* Spring Boot
* Spring Data JPA
* MySQL
* AWS SQS
* Docker
* Swagger/OpenAPI

---

## Settlement Service

Responsável pelo processamento financeiro das ordens.

### Funcionalidades

* Consumo de eventos do SQS
* Liquidação financeira
* Cálculo de taxas
* Idempotência
* Circuit Breaker
* Retry
* Publicação de eventos downstream

### Tecnologias

* Java 21
* Spring Boot
* MySQL
* AWS SQS
* Resilience4j
* Docker

---

## Notification Service

Responsável pelo processamento de notificações assíncronas.

### Funcionalidades

* Consumo de eventos SQS
* Simulação de notificações
* Logs distribuídos
* Métricas de processamento

### Tecnologias

* Java 21
* Spring Boot
* AWS SQS
* LocalStack

---

## Statement Service

Responsável pela geração e armazenamento de comprovantes de liquidação.

### Funcionalidades

* Consumo de eventos SQS
* Geração de comprovantes
* Persistência em banco
* Upload para AWS S3
* Download de comprovantes
* Métricas customizadas

### Tecnologias

* Java 21
* Spring Boot
* MySQL
* AWS S3
* LocalStack

---

# Segurança

Autenticação centralizada utilizando JWT.

### Funcionalidades

* Login autenticado
* Geração de token JWT
* Proteção de endpoints
* Controle de acesso por roles

---

# Observabilidade

## Zipkin

Distributed Tracing entre microsserviços.

URL:

http://localhost:9411

---

## Prometheus

Coleta de métricas da aplicação.

URL:

http://localhost:9090

Métricas monitoradas:

* Requests HTTP
* JVM Metrics
* CPU
* Memória
* Métricas customizadas

---

## Grafana

Visualização de métricas e dashboards.

URL:

http://localhost:3000

---

# Métricas Customizadas

Statement Service:

* statement_processing_success_total
* statement_processing_error_total
* statement_s3_upload_success_total
* statement_s3_upload_error_total

---

# Infraestrutura

## Docker

Todos os microsserviços possuem Dockerfile dedicado.

### Serviços de Infraestrutura

* MySQL
* LocalStack
* Zipkin
* Prometheus
* Grafana

---

## Terraform

Infraestrutura AWS provisionada automaticamente.

### Recursos Provisionados

SQS

* order-created-queue
* order-created-dlq
* notification-queue
* statement-queue
* statement-dlq

S3

* investment-statements

Com Terraform não é necessário criar recursos manualmente.

---

# CI/CD

Pipeline automatizada utilizando GitHub Actions.

### Funcionalidades

* Build automático
* Maven Build Reutilizável
* Java 21
* Validação de Pull Requests

---

# Tecnologias Utilizadas

## Backend

* Java 21
* Spring Boot 3
* Spring Security
* Spring Cloud Gateway
* Spring Data JPA
* Hibernate
* Maven

## Banco de Dados

* MySQL

## Mensageria

* AWS SQS
* AWS SNS
* LocalStack

## Armazenamento

* AWS S3
* LocalStack

## Observabilidade

* Prometheus
* Grafana
* Zipkin
* Micrometer

## Infraestrutura

* Docker
* Docker Compose
* Terraform

## DevOps

* GitHub Actions

## Testes

* JUnit 5
* Mockito

---

# Estrutura do Projeto

```text
invest-settlement-hub
│
├── backend
│   ├── gateway-service
│   ├── order-service
│   ├── settlement-service
│   ├── notification-service
│   └── statement-service
│
├── infra
│   ├── docker-compose.yml
│   ├── terraform
│   ├── prometheus
│   └── grafana
│
└── .github
    └── workflows
```

---

# Como Executar

## Pré-requisitos

* Java 21
* Maven
* Docker Desktop
* Terraform
* AWS CLI
* Git

---

## Subir Infraestrutura

```bash
docker compose up -d
```

---

## Provisionar Recursos AWS LocalStack

```bash
cd infra/terraform

terraform init
terraform apply
```

---

## Executar Microsserviços

```bash
mvn spring-boot:run
```

Executar individualmente:

* gateway-service
* order-service
* settlement-service
* notification-service
* statement-service

---

# Fluxo da Aplicação

1. Cliente cria uma ordem
2. Order Service persiste a ordem
3. Evento é publicado no SQS
4. Settlement Service processa a liquidação
5. Notification Service recebe evento de notificação
6. Statement Service gera comprovante
7. Comprovante é armazenado no S3
8. Métricas são enviadas ao Prometheus
9. Traces são enviados ao Zipkin

---

# Próximas Evoluções

* Kubernetes
* Testcontainers
* OpenTelemetry
* Deploy em AWS
* Dashboards avançados Grafana
* Testes de integração distribuída

---

# Autor

## Thais Scheiner

Desenvolvedora Full Stack com foco em:

* Java
* Spring Boot
* Microsserviços
* AWS
* Arquitetura Backend
* Sistemas Distribuídos
* Observabilidade
* DevOps

LinkedIn:
https://www.linkedin.com/in/thaisscheiner/


