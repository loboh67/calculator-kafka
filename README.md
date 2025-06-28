# WIT Calculator API

Uma API RESTful para operações aritméticas básicas com comunicação assíncrona via Kafka.

## 🚀 Como correr o projeto

### Pré-requisitos

- Docker e Docker Compose instalados
- Java 21


### Subir a infraestrutura (Kafka + Zookeeper)
```bash
cd docker
docker-compuse up -d
```

### Correr os serviços Calculator e REST
```bash
./gradlew :calculator:bootRun
./gradlew :rest:bootRun
```

## Testar a API

Exemplo de request com curl:
```bash
curl -i "http://localhost:8080/sum?a=2&b=2"   
```

Exemplo de resposta:
```bash
HTTP/1.1 200 
X-Request-ID: 1a833eb9-f59b-45e2-a5ff-fc4a7783a30f
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 28 Jun 2025 14:22:31 GMT

{"result":4}
```