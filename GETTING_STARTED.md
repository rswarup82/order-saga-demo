# Getting Started with Order Saga Demo

This guide will walk you through setting up and running the Order Saga Demo application.

## 📋 Step-by-Step Setup

### Step 1: Prerequisites Check

Ensure you have the following installed:

```bash
# Check Java version (requires 17+)
java -version

# Check Maven version
mvn -version

# Check Docker
docker --version
docker-compose --version
```

### Step 2: Clone/Extract the Project

Navigate to the project directory:

```bash
cd order-saga-demo
```

### Step 3: Start Temporal Server

Start Temporal using Docker Compose:

```bash
docker-compose up -d
```

Wait for services to start (about 30 seconds). Verify:

```bash
docker-compose ps
```

You should see:
- `temporal` - Running on port 7233
- `temporal-ui` - Running on port 8088
- `temporal-postgresql` - Running on port 5432

Access Temporal UI at: http://localhost:8088

### Step 4: Build the Application

```bash
mvn clean install
```

This will:
- Download all dependencies
- Compile the code
- Run tests (if any)
- Create the application JAR

### Step 5: Start the Application

```bash
mvn spring-boot:run
```

Wait for the application to start. You should see:

```
Started OrderSagaDemoApplication in X.XXX seconds
Temporal worker started successfully
```

The application is now running on http://localhost:8080

### Step 6: Verify Setup

#### Test the API

```bash
curl http://localhost:8080/api/orders
```

Should return: `[]` (empty array)

#### Check H2 Console

1. Open http://localhost:8080/h2-console
2. Use these credentials:
   - JDBC URL: `jdbc:h2:mem:ordersdb`
   - Username: `sa`
   - Password: (leave empty)
3. Click "Connect"

## 🧪 Running Your First Order

### Method 1: Using cURL

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
      }
    ],
    "totalAmount": 1200.00
  }'
```

### Method 2: Using Sample Requests

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d @sample-requests/order-electronics.json
```

### Method 3: Using Test Script

```bash
./test-orders.sh
```

This will:
- Create 3 sample orders
- Wait for them to complete
- Display the status of each order

## 📊 Monitoring Workflows

### 1. Temporal UI

1. Open http://localhost:8088
2. Click on "Workflows"
3. You'll see workflows named `order-workflow-{orderId}`
4. Click on a workflow to view:
   - Execution timeline
   - Activity results
   - Input/output data
   - Compensation steps (if any)

### 2. Application Logs

The console will show detailed logs:

```
Step 1: Creating order ORD-abc12345
Step 2: Authorizing payment for order ORD-abc12345
Payment authorized successfully: PAY-xyz789
Step 3: Reserving inventory for order ORD-abc12345
...
Order completed: ORD-abc12345
```

### 3. H2 Database

Query the database to see order status:

```sql
SELECT order_id, customer_id, status, total_amount, created_at 
FROM orders;
```

## 🔍 Understanding the Output

### Successful Order

```json
{
  "orderId": "ORD-abc12345",
  "customerId": "CUST-001",
  "status": "COMPLETED",
  "totalAmount": 1200.00,
  "paymentId": "PAY-xyz789",
  "reservationId": "RES-def456",
  "shippingId": "SHIP-ghi789",
  "trackingNumber": "TRK1234567890"
}
```

### Failed Order (with Compensation)

```json
{
  "orderId": "ORD-def67890",
  "customerId": "CUST-002",
  "status": "COMPENSATED",
  "totalAmount": 500.00,
  "paymentId": "PAY-abc123",
  "failureReason": "Fraud check failed: High fraud risk detected"
}
```

## 🎯 Testing Different Scenarios

### 1. Successful Order Flow

Most orders will complete successfully. You'll see:
- All 8 steps executing in sequence
- Order status changing from PENDING → COMPLETED
- Tracking number assigned

### 2. Payment Failure (10% chance)

```
Step 2: Payment authorization failed
Starting compensation...
Compensation completed
Order status: FAILED
```

### 3. Inventory Failure (5% chance)

```
Step 3: Inventory reservation failed
Starting compensation...
Compensating payment...
Order status: COMPENSATED
```

### 4. Fraud Check Failure (3% chance)

```
Step 4: Fraud check failed - High risk score
Starting compensation...
Compensating inventory...
Compensating payment...
Order status: COMPENSATED
```

## 📈 Query Orders

### Get Specific Order

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
curl http://localhost:8080/api/orders/status/FAILED
curl http://localhost:8080/api/orders/status/COMPENSATED
```

## 🛑 Stopping the Application

### Stop Spring Boot Application

Press `Ctrl+C` in the terminal running the application

### Stop Temporal

```bash
docker-compose down
```

To also remove volumes:

```bash
docker-compose down -v
```

## 🔧 Troubleshooting

### Application won't start - Port already in use

```bash
# Change application port
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

Or edit `application.properties`:
```properties
server.port=8081
```

### Can't connect to Temporal

```bash
# Check if Temporal is running
docker-compose ps

# View Temporal logs
docker-compose logs temporal

# Restart Temporal
docker-compose restart temporal
```

### Worker not processing workflows

Check the logs for:
```
Temporal worker started successfully
```

If not present, verify:
1. Temporal server is running
2. Task queue name matches in configuration
3. No connection errors in logs

### Database errors

The H2 database is in-memory and resets on restart. This is expected behavior.

## 📚 Next Steps

Now that you have the application running:

1. **Explore the Code**:
   - Review `OrderWorkflowImpl.java` for saga logic
   - Check `OrderActivitiesImpl.java` for business logic
   - Understand compensation in action

2. **Experiment**:
   - Create orders with different amounts
   - Observe failure scenarios
   - Watch compensation in Temporal UI

3. **Extend**:
   - Add new steps to the workflow
   - Implement custom compensation logic
   - Add more validation rules

4. **Learn More**:
   - Study Temporal documentation
   - Understand saga patterns
   - Explore advanced Temporal features

## 💡 Tips

- **Temporal UI** is your best friend for debugging workflows
- **Application logs** show detailed step-by-step execution
- **H2 Console** lets you inspect database state
- Create **multiple orders** to observe different failure scenarios
- Each workflow execution is **isolated** and **recoverable**

## 🎓 Key Learnings

After running through this demo, you should understand:

1. ✅ How saga pattern handles distributed transactions
2. ✅ How Temporal orchestrates long-running workflows
3. ✅ How compensation works in practice
4. ✅ How to build resilient microservices architectures
5. ✅ How to monitor and debug distributed workflows

---

**Ready to build production-grade saga orchestrators? You've got this! 🚀**
