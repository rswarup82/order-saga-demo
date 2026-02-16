# Quick Reference Guide

## 🚀 Quick Commands

### Start Everything

```bash
# 1. Start Temporal
docker-compose up -d

# 2. Start Application
mvn spring-boot:run

# 3. Run tests
./test-orders.sh
```

### Stop Everything

```bash
# Stop application: Ctrl+C

# Stop Temporal
docker-compose down
```

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Create new order |
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{orderId}` | Get order by ID |
| GET | `/api/orders/customer/{customerId}` | Get customer orders |
| GET | `/api/orders/status/{status}` | Get orders by status |

## 🔗 URLs

- **Application**: http://localhost:8080
- **Temporal UI**: http://localhost:8088
- **H2 Console**: http://localhost:8080/h2-console
- **Temporal Server**: localhost:7233

## 📊 Order Status Values

| Status | Description |
|--------|-------------|
| `PENDING` | Order created, workflow starting |
| `PAYMENT_AUTHORIZED` | Payment successful |
| `INVENTORY_RESERVED` | Inventory locked |
| `FRAUD_CHECK_PASSED` | Fraud check passed |
| `CONFIRMED` | Order confirmed |
| `SHIPPING_ARRANGED` | Shipping created |
| `IN_DELIVERY` | Out for delivery |
| `COMPLETED` | Successfully delivered |
| `FAILED` | Order failed before compensation |
| `COMPENSATING` | Rolling back changes |
| `COMPENSATED` | Rollback completed |

## 🎯 Workflow Steps

1. **Create Order** → Persist to DB
2. **Authorize Payment** → 10% failure rate
3. **Reserve Inventory** → 5% failure rate
4. **Fraud Check** → 3% failure rate
5. **Confirm Order** → Mark confirmed
6. **Arrange Shipping** → 2% failure rate
7. **Delivery Tracking** → Update status
8. **Complete Order** → Final state

## 💾 H2 Console Access

```
URL: jdbc:h2:mem:ordersdb
User: sa
Password: (empty)
```

### Useful Queries

```sql
-- View all orders
SELECT * FROM orders;

-- View order items
SELECT * FROM order_items;

-- Orders by status
SELECT order_id, status, total_amount 
FROM orders 
WHERE status = 'COMPLETED';

-- Failed orders
SELECT order_id, status, failure_reason 
FROM orders 
WHERE status IN ('FAILED', 'COMPENSATED');
```

## 🧪 Sample cURL Commands

### Create Simple Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-001",
    "items": [{
      "productId": "PROD-001",
      "productName": "Product Name",
      "quantity": 1,
      "unitPrice": 100.00
    }],
    "totalAmount": 100.00
  }'
```

### Get Order Status

```bash
curl http://localhost:8080/api/orders/ORD-abc12345 | jq
```

### List All Orders

```bash
curl http://localhost:8080/api/orders | jq
```

### Filter by Status

```bash
curl http://localhost:8080/api/orders/status/COMPLETED | jq
```

## 🐛 Debug Checklist

- [ ] Is Temporal running? `docker-compose ps`
- [ ] Is application started? Check logs for "Started OrderSagaDemoApplication"
- [ ] Is worker registered? Check logs for "Temporal worker started successfully"
- [ ] Check Temporal UI for workflow execution
- [ ] Review application logs for errors
- [ ] Verify H2 database has orders

## 🔧 Configuration Files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven dependencies |
| `application.properties` | App configuration |
| `docker-compose.yml` | Temporal setup |

## 📦 Key Classes

| Class | Purpose |
|-------|---------|
| `OrderWorkflowImpl` | Saga orchestration |
| `OrderActivitiesImpl` | Business logic |
| `OrderService` | Workflow starter |
| `OrderController` | REST endpoints |
| `TemporalConfig` | Temporal setup |

## ⚙️ Configuration Properties

```properties
# Application
server.port=8080

# Temporal
temporal.service.url=localhost:7233
temporal.namespace=default
temporal.task-queue=order-processing-queue

# Database
spring.datasource.url=jdbc:h2:mem:ordersdb
```

## 📝 Log Messages to Watch For

### Success

```
✅ Order created successfully: ORD-xxx
✅ Payment authorized successfully: PAY-xxx
✅ Inventory reserved successfully: RES-xxx
✅ Fraud check passed for order: ORD-xxx
✅ Order completed: ORD-xxx
```

### Failure & Compensation

```
⚠️  Payment authorization failed for order: ORD-xxx
⚠️  Inventory reservation failed for order: ORD-xxx
⚠️  Fraud check failed for order: ORD-xxx
🔄 Starting compensation for order: ORD-xxx
🔄 Compensating payment: PAY-xxx
🔄 Compensating inventory reservation: RES-xxx
✅ Compensation completed for order: ORD-xxx
```

## 🎓 Common Scenarios

### Scenario 1: Everything Works ✅
- All steps complete
- Status: `COMPLETED`
- Has: paymentId, reservationId, shippingId, trackingNumber

### Scenario 2: Payment Fails ❌
- Fails at step 2
- No compensation needed (nothing to roll back)
- Status: `FAILED`

### Scenario 3: Inventory Fails ❌
- Fails at step 3
- Compensates: Payment refund
- Status: `COMPENSATED`

### Scenario 4: Fraud Check Fails ❌
- Fails at step 4
- Compensates: Inventory release, Payment refund
- Status: `COMPENSATED`

## 💡 Pro Tips

1. **Watch Temporal UI** for real-time workflow visualization
2. **Create multiple orders** to see different failure scenarios
3. **Check H2 Console** to verify database state
4. **Review logs** for detailed execution flow
5. **Use sample requests** for quick testing

## 🚨 Troubleshooting Quick Fixes

| Problem | Solution |
|---------|----------|
| Port 8080 in use | Change port: `--server.port=8081` |
| Temporal not connecting | `docker-compose restart temporal` |
| Worker not starting | Check logs for errors, verify task queue |
| Orders not processing | Verify worker is running in logs |

---

**Keep this guide handy for quick reference! 📖**
