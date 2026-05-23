# Invest Settlement Hub

Plataforma distribuída de liquidação financeira inspirada em fluxos reais utilizados por bancos, corretoras e instituições financeiras modernas.

O objetivo deste projeto é demonstrar conhecimentos avançados em arquitetura de microsserviços, mensageria assíncrona, observabilidade, resiliência, segurança e sistemas cloud-native utilizando Java 21 e ecossistema Spring.

---

# Visão Geral

O sistema simula o fluxo completo de processamento de investimentos, incluindo:

- Recebimento de ordens de compra e venda
- Processamento assíncrono de liquidação financeira
- Publicação de eventos entre microsserviços
- Notificações distribuídas
- Rastreabilidade distribuída
- Observabilidade e monitoramento
- Segurança com JWT
- Comunicação orientada a eventos

A arquitetura foi construída seguindo princípios utilizados em sistemas financeiros de alta disponibilidade e alta escalabilidade.

---

# Arquitetura da Aplicação

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
AWS SQS (LocalStack)
   │
   ▼
Settlement Service
   │
   ▼
Notification Service

Observabilidade:
- Zipkin
- Prometheus
- Grafana
```

---

# Microsserviços

## API Gateway

Responsável por:

- Autenticação JWT
- Roteamento das requisições
- Centralização de acesso
- Segurança entre serviços
- Distributed tracing

### Tecnologias

- Spring Cloud Gateway
- Spring Security
- JWT
- Micrometer
- Zipkin

---

## Order Service

Responsável pelo registro de ordens de investimento.

### Funcionalidades

- Cadastro de ordens
- Consulta de ordens
- Publicação de eventos no SQS
- Logs estruturados
- Correlation ID

### Fluxo

1. Cliente envia ordem
2. Ordem é persistida no MySQL
3. Evento é publicado no SQS
4. Settlement Service consome o evento

### Tecnologias

- Java 21
- Spring Boot
- MySQL
- AWS SQS
- Docker
- JPA/Hibernate
- Swagger/OpenAPI

---

## Settlement Service

Responsável pela liquidação financeira assíncrona.

### Funcionalidades

- Consumo de eventos do SQS
- Cálculo financeiro
- Aplicação de taxas
- Retry com backoff
- Idempotência
- Dead Letter Queue (DLQ)
- Publicação de eventos de liquidação

### Fluxo

1. Consome evento do SQS
2. Processa liquidação
3. Salva resultado
4. Publica evento para notification-service

### Tecnologias

- Java 21
- Spring Boot
- AWS SQS
- Resilience4j
- MySQL
- Docker

---

## Notification Service

Responsável pelo processamento de notificações assíncronas.

### Funcionalidades

- Consumo de mensagens via SQS
- Simulação de envio de notificações
- Logs distribuídos
- Rastreamento completo da operação

### Tecnologias

- Java 21
- Spring Boot
- AWS SQS
- LocalStack

---

# Observabilidade

A aplicação possui stack completa de observabilidade.

## Zipkin

Responsável pelo distributed tracing entre os microsserviços.

Permite rastrear:

- Fluxo completo da requisição
- Tempo entre serviços
- Correlation IDs
- Latência distribuída

### URL

```bash
http://localhost:9411
```

---

## Prometheus

Responsável pela coleta de métricas.

### Métricas monitoradas

- Requests HTTP
- Latência
- JVM Metrics
- Uso de memória
- Uso de CPU

### URL

```bash
http://localhost:9090
```

---

## Grafana

Responsável pela visualização das métricas.

### URL

```bash
http://localhost:3000
```

---

# Segurança

O sistema utiliza autenticação JWT.

## Funcionalidades

- Login autenticado
- Geração de token JWT
- Proteção de endpoints
- Validação centralizada no gateway

---

# Arquitetura Event-Driven

A comunicação entre serviços ocorre de forma assíncrona utilizando mensageria.

## Benefícios

- Baixo acoplamento
- Escalabilidade
- Resiliência
- Processamento assíncrono
- Tolerância a falhas

---

# Tecnologias Utilizadas

## Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Cloud Gateway
- Spring Data JPA
- Hibernate
- Maven

## Banco de Dados

- MySQL

## Cloud / Mensageria

- AWS SQS
- AWS SNS
- LocalStack

## Observabilidade

- Zipkin
- Prometheus
- Grafana
- Micrometer

## Infraestrutura

- Docker
- Docker Compose

## Testes

- JUnit
- Mockito

---

# Estrutura do Projeto

```text
invest-settlement-hub/
│
├── backend/
│   ├── gateway-service/
│   ├── order-service/
│   ├── settlement-service/
│   └── notification-service/
│
├── infra/
│   ├── docker-compose.yml
│   ├── prometheus/
│   └── grafana/
│
└── README.md
```

---

# Como Executar o Projeto

## Pré-requisitos

- Java 21
- Docker Desktop
- Maven
- AWS CLI
- Git

---

# 1. Clonar o Repositório

```bash
git clone https://github.com/SEU-USUARIO/invest-settlement-hub.git
```

---

# 2. Subir Infraestrutura

```bash
cd infra

docker compose up -d
```

---

# 3. Executar Microsserviços

Executar individualmente:

- gateway-service
- order-service
- settlement-service
- notification-service

---

# 4. Criar Filas SQS

```bash
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name order-created-queue
```

```bash
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name order-created-dlq
```

```bash
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name notification-queue
```

---

# 5. Testar Aplicação

## Criar Ordem

```http
POST /orders
```

### Exemplo

```json
{
  "customerId": "customer-1",
  "assetCode": "PETR4",
  "operationType": "BUY",
  "quantity": 10,
  "unitPrice": 30.00
}
```

---

# Fluxo Completo

## 1. Order Service

- Recebe requisição
- Salva ordem
- Publica evento

## 2. Settlement Service

- Consome evento
- Processa liquidação
- Aplica taxas
- Publica notificação

## 3. Notification Service

- Consome evento
- Processa notificação
- Finaliza fluxo

---

# Funcionalidades Implementadas

- Microsserviços
- JWT Authentication
- API Gateway
- Event-Driven Architecture
- AWS SQS
- Retry com Backoff
- DLQ
- Idempotência
- Distributed Tracing
- Observabilidade
- Logs Estruturados
- Dockerização
- Comunicação Assíncrona
- Monitoramento
- Correlation IDs

---

# Roadmap Futuro

## Próximas Implementações

- Kubernetes
- CI/CD com GitHub Actions
- Terraform
- ECS Deployment
- OpenTelemetry
- Kafka
- Statement Service
- Upload de documentos para S3
- Frontend Angular 19
- Testcontainers
- Testes de integração distribuída

---

# Objetivo do Projeto

Este projeto foi desenvolvido com foco em:

- Arquitetura distribuída
- Sistemas financeiros
- Cloud-native applications
- Microsserviços modernos
- Resiliência
- Observabilidade
- Event-driven systems

Inspirado em arquiteturas utilizadas por fintechs, bancos digitais e plataformas de investimento.

---

# Autor

## Thais Scheiner

Desenvolvedora Full Stack focada em:

- Java
- Microsserviços
- Cloud
- Sistemas Distribuídos
- Arquitetura Backend
- AWS
- Observabilidade

### LinkedIn

```text
https://www.linkedin.com/in/thaisscheiner/
```

### GitHub

```text
https://github.com/ThaisScheiner
```