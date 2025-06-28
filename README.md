# Distributed Calculator Service

This project implements a distributed calculator using Spring Boot and Kafka. It consists of two microservices:

- **REST Service**: Exposes an HTTP API to receive calculation requests from clients.
- **Calculator Service**: Consumes calculation requests from Kafka, performs the computation, and sends back the result.

---

## 🧱 Architecture
Client → REST API → Kafka Topic → Calculator Service → Kafka Response → REST API (response)

Each HTTP request is assigned a unique `requestId` which is propagated throughout the system using MDC for traceability.

---

## 🚀 Features

- Supports `sum`, `subtract`, `multiply`, and `divide`
- Uses Kafka for asynchronous request/response communication
- MDC-based logging with `requestId` tracking
- Includes unit tests 

---

## 📦 Project Structure