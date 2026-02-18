# Payment Orchestrator Engine 🚀

![Java 21](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot 4](https://img.shields.io/badge/Spring_Boot-4.0-brightgreen?style=flat-square&logo=springboot)
![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-blue?style=flat-square)

A high-throughput payment orchestration engine designed for mission-critical transaction processing with minimal latency. This engine leverages **Java 21 (Project Loom)** for massive concurrency and a clean architecture to ensure long-term maintainability.

## 🏗️ Architecture & Technical Stack

The project follows **Hexagonal Architecture (Ports & Adapters)** principles, keeping the business core isolated from external providers, databases, and message brokers.



* **Runtime:** Java 21 with **Virtual Threads** (Loom) for a lightweight, non-blocking I/O model.
* **Framework:** Spring Boot 4.0 (optimized for Virtual Threads and cloud-native auto-configuration).
* **Ingestion:** Native multi-adapter support via **REST API** and **RabbitMQ Consumer**.
* **Persistence:** NoSQL with **MongoDB** for transaction logging and audit trails.
* **Security:** Architected under AWS security best practices (IAM, encryption-at-rest/transit).

---

## 🚀 Key Features

### 1. Massive Concurrency with Virtual Threads
Moving beyond the traditional *thread-per-request* model, this engine utilizes virtual threads to scale linearly without the overhead of OS platform threads. This is ideal for I/O-bound payment orchestrations.



### 2. Rich Domain & Strong Typing
Leveraging Modern Java features for more expressive and error-proof code:
* **Records:** For immutable DTOs and Value Objects.
* **Sealed Classes & Interfaces:** To model payment states (Pending, Authorized, Declined) with exhaustive switch expressions.
* **Pattern Matching:** For clean and safe orchestration logic.

### 3. Resilience & High Availability
Configured with asynchronous retries and message queuing to ensure zero transaction loss during external service outages.

---

## 🛠️ Local Setup (OrbStack / Docker)

The development environment is fully containerized to ensure parity with production.

### Spin up Infrastructure
```bash
docker compose up -d
```
This initializes:

MongoDB: localhost:27017 (AuthSource: admin)

RabbitMQ: localhost:5672 (Management UI: http://localhost:15672)

### App Configuration (application.yaml)
The engine uses the updated Spring Boot 4 property namespace:

```yaml
spring:
  threads:
    virtual:
      enabled: true
  mongodb:
    uri: mongodb://${MONGO_USER:root}:${MONGO_PASS:root_pass}@localhost:27017/payment-engine?authSource=admin
  rabbitmq:
    host: localhost
```
## 📂 Project Structure
```Plaintext
payment-orchestrator-engine/
├── src/main/java/com/payments/
│   ├── domain/         # Pure logic (Entities, Value Objects, Sealed Classes)
│   ├── application/    # Use cases and Port interfaces (Input/Output)
│   └── infrastructure/ # Adapters (REST, RabbitMQ, MongoDB Config)
├── docker-compose.yaml # Local infrastructure
└── README.md
```
## 🧪 Testing & Mocking

The system includes a `StubPaymentGateway` to simulate different provider responses based on the transaction `amount`. Use these thresholds to test your business logic:

| Amount Range (COP) | Gateway Response | Description |
| :--- | :--- | :--- |
| `< 1000` | **Approved** ✅ | Standard successful transaction. |
| `1000 - 4999` | **Rejected** ❌ | Simulated business rejection (e.g., insufficient funds). |
| `>= 5000` | **FraudAlert** ⚠️ | Triggers internal fraud orchestration flow. |



### Test Payload Example (Approved)
```json
{
  "currency": "COP",
  "amount": 500.00,
  "paymentMethodType": "CREDIT_CARD",
  "methodDetails": { "bin": "457173" }
}
```
## ✒️ Author

Santiago Galvis (Samic05)