# Order Saga Demo - Spring Boot + Temporal.io

A real-world demonstration of the **Saga Pattern** using **Spring Boot** and **Temporal.io** for distributed transaction orchestration in a microservices architecture.

## 🎯 Use Case: E-Commerce Order Processing

This application demonstrates a complete order processing workflow with the following steps:

```
Order Placed
    ↓
Payment Authorization
    ↓
Inventory Reservation
    ↓
Fraud Check
    ↓
Order Confirmation
    ↓
Shipping Arrangement
    ↓
Delivery Tracking
    ↓
Order Completed
```

### 🔄 Saga Pattern Features

- **Automatic Compensation**: If any step fails, compensating transactions automatically roll back previous steps
- **Distributed Transactions**: Coordinates multiple services without 2PC (Two-Phase Commit)
- **Reliability**: Temporal ensures workflow execution even if the application crashes
- **Observability**: Track workflow progress through Temporal UI

## 📋 Prerequisites

- **Java 17+**
- **Maven 3.6+**
- **Docker & Docker Compose** (for Temporal server)

## 🚀 Quick Start

### 1. Start Temporal Server

Start Temporal using Docker Compose:

```bash
curl -O https://raw.githubusercontent.com/temporalio/temporal/master/docker/docker-compose.yml
docker-compose up -d
```

Verify Temporal is running:
- Temporal Server: `localhost:7233`
- Temporal UI: http://localhost:8080

### 2. Build the Application

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080` (or `8081` if Temporal UI is on 8080).

### 4. Access H2 Console (Optional)

- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:ordersdb`
- Username: `sa`
- Password: (leave empty)

## 📡 API Endpoints

### Create an Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-001",
    "items": [
      {
        "productId": "PROD-001",
        "productName": "Laptop",
        "quantity": 1,
        "unitPrice": 1200.00
      },
      {
        "productId": "PROD-002",
        "productName": "Mouse",
        "quantity": 2,
        "unitPrice": 25.00
      }
    ],
    "totalAmount": 1250.00
  }'
```

Response:
```json
{
  "orderId": "ORD-abc12345",
  "message": "Order workflow started successfully",
  "status": "PROCESSING"
}
```

### Get Order Status

```bash
curl http://localhost:8080/api/orders/ORD-abc12345
```

### Get All Orders

```bash
curl http://localhost:8080/api/orders
```

### Get Orders by Customer

```bash
curl http://localhost:8080/api/orders/customer/CUST-001
```

### Get Orders by Status

```bash
curl http://localhost:8080/api/orders/status/COMPLETED
```

## 🏗️ Architecture

### Key Components

#### 1. **Workflow (Saga Orchestrator)**
- `OrderWorkflow` - Defines the workflow interface
- `OrderWorkflowImpl` - Implements the saga orchestration logic
- Coordinates all steps and handles compensation

#### 2. **Activities (Business Logic)**
- `OrderActivities` - Interface defining all business operations
- `OrderActivitiesImpl` - Implements actual business logic for each step
- Each activity is idempotent and can be retried

#### 3. **Domain Model**
- `Order` - Main order entity
- `OrderItem` - Order line items
- `OrderStatus` - Order state enum
- Result DTOs for each step (PaymentResult, InventoryResult, etc.)

#### 4. **Service Layer**
- `OrderService` - Orchestrates workflow execution
- Integrates with Temporal WorkflowClient

#### 5. **REST API**
- `OrderController` - Exposes REST endpoints
- Handles order creation and queries

### Workflow Steps Explained

1. **Create Order**: Persists order to database
2. **Authorize Payment**: Validates and authorizes payment (10% failure rate for demo)
3. **Reserve Inventory**: Checks and reserves inventory (5% failure rate)
4. **Fraud Check**: Performs fraud detection (3% failure rate)
5. **Confirm Order**: Marks order as confirmed
6. **Arrange Shipping**: Creates shipment and gets tracking number (2% failure rate)
7. **Update Delivery Tracking**: Updates order with delivery status
8. **Complete Order**: Marks order as completed

### Compensation Logic

If any step fails after Payment Authorization, the saga automatically triggers compensations in reverse order:

```
Failure Detected
    ↓
Cancel Shipping (if arranged)
    ↓
Release Inventory (if reserved)
    ↓
Refund Payment (if authorized)
    ↓
Mark Order as FAILED/COMPENSATED
```

## 📊 Monitoring Workflows

### Temporal Web UI

1. Open http://localhost:8080 (Temporal UI)
2. Navigate to "Workflows"
3. Search for workflow ID: `order-workflow-{orderId}`
4. View:
   - Workflow execution history
   - Activity progress
   - Compensation steps (if failure occurred)
   - Input/output payloads

### Application Logs

The application logs provide detailed information about:
- Workflow steps execution
- Activity invocations
- Compensation triggers
- Business logic decisions

## 🧪 Testing Scenarios

### Successful Order

```bash
# Create an order
ORDER_ID=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-001",
    "items": [{
      "productId": "PROD-001",
      "productName": "Laptop",
      "quantity": 1,
      "unitPrice": 1200.00
    }],
    "totalAmount": 1200.00
  }' | jq -r '.orderId')

# Wait a few seconds for workflow to complete
sleep 10

# Check order status
curl http://localhost:8080/api/orders/$ORDER_ID
```

### Testing Failures

The application simulates random failures:
- **Payment**: 10% failure rate
- **Inventory**: 5% failure rate
- **Fraud Check**: 3% failure rate (high risk scores)
- **Shipping**: 2% failure rate

Create multiple orders to observe different failure scenarios and compensation.

## 🔧 Configuration

Edit `application.properties` to customize:

```properties
# Change application port
server.port=8081

# Temporal server connection
temporal.service.url=localhost:7233
temporal.namespace=default
temporal.task-queue=order-processing-queue

# Database settings
spring.datasource.url=jdbc:h2:mem:ordersdb
```

## 📁 Project Structure

```
order-saga-demo/
├── src/main/java/com/example/ordersaga/
│   ├── activities/
│   │   ├── OrderActivities.java
│   │   └── OrderActivitiesImpl.java
│   ├── config/
│   │   └── TemporalConfig.java
│   ├── controller/
│   │   └── OrderController.java
│   ├── model/
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── OrderStatus.java
│   │   ├── OrderRequest.java
│   │   └── [Result DTOs]
│   ├── repository/
│   │   └── OrderRepository.java
│   ├── service/
│   │   └── OrderService.java
│   ├── workflow/
│   │   ├── OrderWorkflow.java
│   │   └── OrderWorkflowImpl.java
│   └── OrderSagaDemoApplication.java
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

## 🎓 Key Concepts Demonstrated

### 1. Saga Pattern
- Coordinates distributed transactions
- Implements compensating transactions
- Maintains data consistency across services

### 2. Temporal Workflows
- Durable execution
- Automatic retries
- State persistence
- Workflow versioning

### 3. Activity Design
- Idempotent operations
- Retry policies
- Timeout handling
- Error propagation

### 4. Spring Integration
- Dependency injection with Temporal
- Spring Data JPA for persistence
- REST API development

## 🔍 Advanced Features

### Retry Configuration

Activities are configured with automatic retries:
```java
RetryOptions.newBuilder()
    .setMaximumAttempts(3)
    .setInitialInterval(Duration.ofSeconds(1))
    .setMaximumInterval(Duration.ofSeconds(10))
    .setBackoffCoefficient(2.0)
    .build()
```

### Saga Options

Saga is configured for sequential compensation:
```java
new Saga.Options.Builder()
    .setParallelCompensation(false)
    .build()
```

### Activity Timeouts

```java
ActivityOptions.newBuilder()
    .setStartToCloseTimeout(Duration.ofMinutes(5))
    .build()
```

## 🐛 Troubleshooting

### Temporal Connection Issues

```bash
# Check if Temporal is running
docker ps | grep temporal

# Check Temporal logs
docker-compose logs temporal
```

### Application Won't Start

```bash
# Check if port 8080 is available
lsof -i :8080

# Use alternative port
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Workflow Not Executing

1. Check Temporal UI for workflow status
2. Verify task queue name matches in config
3. Check application logs for worker registration
4. Ensure worker is running (check logs for "Temporal worker started successfully")

## 📚 Learn More

- [Temporal Documentation](https://docs.temporal.io/)
- [Saga Pattern](https://microservices.io/patterns/data/saga.html)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Temporal Java SDK](https://github.com/temporalio/sdk-java)

## 📝 License

This is a demonstration project for educational purposes.

## 🤝 Contributing

Feel free to extend this demo with:
- Additional workflow steps
- More complex compensation logic
- Integration with real payment/inventory services
- Monitoring and alerting
- Testing with Temporal TestWorkflowEnvironment

## 💡 Next Steps

1. **Add More Services**: Integrate with real payment gateways, inventory systems
2. **Implement Queries**: Add Temporal queries to get real-time workflow status
3. **Add Signals**: Implement workflow signals for dynamic behavior (e.g., cancel order)
4. **Enhanced Monitoring**: Add Prometheus metrics and Grafana dashboards
5. **Child Workflows**: Split complex workflows into child workflows
6. **Versioning**: Implement workflow versioning for production deployments

---

**Happy Saga Orchestrating! 🚀**
