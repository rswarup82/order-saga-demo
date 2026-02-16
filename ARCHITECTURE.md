# Architecture Overview

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client / API Consumer                    │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP REST
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Spring Boot Application                     │
│                                                                   │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    OrderController                        │  │
│  │                    (REST Endpoints)                       │  │
│  └─────────────────────────┬────────────────────────────────┘  │
│                             │                                    │
│  ┌─────────────────────────▼────────────────────────────────┐  │
│  │                      OrderService                         │  │
│  │                (Workflow Orchestrator)                    │  │
│  └─────────────────────────┬────────────────────────────────┘  │
│                             │                                    │
│                             │ Start Workflow                     │
│                             ▼                                    │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                   Temporal Worker                         │  │
│  │  ┌────────────────────────────────────────────────────┐  │  │
│  │  │           OrderWorkflowImpl (Saga)                 │  │  │
│  │  │  - Orchestrates workflow steps                     │  │  │
│  │  │  - Manages saga compensations                      │  │  │
│  │  │  - Handles failure scenarios                       │  │  │
│  │  └────────────────────────────────────────────────────┘  │  │
│  │  ┌────────────────────────────────────────────────────┐  │  │
│  │  │           OrderActivitiesImpl                      │  │  │
│  │  │  - Business logic implementation                   │  │  │
│  │  │  - Database operations                             │  │  │
│  │  │  - External service calls (simulated)              │  │  │
│  │  └────────────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────────┘  │
│                             │                                    │
│  ┌─────────────────────────▼────────────────────────────────┐  │
│  │                   OrderRepository                         │  │
│  │                   (Spring Data JPA)                       │  │
│  └─────────────────────────┬────────────────────────────────┘  │
│                             │                                    │
│  ┌─────────────────────────▼────────────────────────────────┐  │
│  │                    H2 Database                            │  │
│  │                  (In-Memory Storage)                      │  │
│  └──────────────────────────────────────────────────────────┘  │
└───────────────────────────┬─────────────────────────────────────┘
                             │
                             │ gRPC
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                       Temporal Server                            │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    Workflow Engine                        │  │
│  │  - Workflow state management                              │  │
│  │  - Task distribution                                      │  │
│  │  - Event history                                          │  │
│  │  - Retry orchestration                                    │  │
│  └──────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                  PostgreSQL Database                      │  │
│  │  - Workflow state persistence                             │  │
│  │  - Event history storage                                  │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

## Workflow Execution Flow

```
                           ┌─────────────────┐
                           │  Order Created  │
                           └────────┬────────┘
                                    │
                     ┌──────────────▼──────────────┐
                     │  Start Temporal Workflow   │
                     └──────────────┬──────────────┘
                                    │
                     ┌──────────────▼──────────────┐
                     │   Create Order Activity     │
                     │   Status: PENDING           │
                     └──────────────┬──────────────┘
                                    │
                     ┌──────────────▼───────────────┐
                     │ Authorize Payment Activity   │
                     │ Status: PAYMENT_AUTHORIZED   │
                     └──────────┬───────────────────┘
                                │
                     ┌──────────▼────────────────────┐
                     │ Reserve Inventory Activity    │
                     │ Status: INVENTORY_RESERVED    │
                     └──────────┬────────────────────┘
                                │
                     ┌──────────▼───────────────────┐
                     │  Fraud Check Activity        │
                     │  Status: FRAUD_CHECK_PASSED  │
                     └──────────┬───────────────────┘
                                │
                     ┌──────────▼──────────────┐
                     │ Confirm Order Activity  │
                     │ Status: CONFIRMED       │
                     └──────────┬──────────────┘
                                │
                     ┌──────────▼─────────────────┐
                     │ Arrange Shipping Activity  │
                     │ Status: SHIPPING_ARRANGED  │
                     └──────────┬─────────────────┘
                                │
                     ┌──────────▼──────────────────┐
                     │ Update Tracking Activity    │
                     │ Status: IN_DELIVERY         │
                     └──────────┬──────────────────┘
                                │
                     ┌──────────▼──────────────┐
                     │ Complete Order Activity │
                     │ Status: COMPLETED       │
                     └─────────────────────────┘
```

## Saga Compensation Flow

```
                        ┌──────────────┐
                        │ Failure      │
                        │ Detected     │
                        └──────┬───────┘
                               │
                ┌──────────────▼──────────────┐
                │  Trigger Saga Compensation  │
                └──────────────┬──────────────┘
                               │
        ┌──────────────────────┴──────────────────────┐
        │                                              │
        ▼                                              │
┌────────────────────┐                                 │
│ Compensate Step N  │                                 │
│ (Last executed)    │                                 │
└────────┬───────────┘                                 │
         │                                             │
         ▼                                             │
┌────────────────────┐                                 │
│ Compensate Step    │                                 │
│ N-1                │                                 │
└────────┬───────────┘                                 │
         │                                             │
         ▼                                             │
┌────────────────────┐                                 │
│ Continue reversing │                                 │
│ in order...        │                                 │
└────────┬───────────┘                                 │
         │                                             │
         ▼                                             │
┌────────────────────┐                                 │
│ Compensate Step 2  │                                 │
│ (Inventory)        │◄────────────────────────────────┤
└────────┬───────────┘                                 │
         │                                             │
         ▼                                             │
┌────────────────────┐                                 │
│ Compensate Step 1  │                                 │
│ (Payment)          │◄────────────────────────────────┘
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│ Mark Order as      │
│ COMPENSATED        │
└────────────────────┘
```

## Component Interaction Sequence

```
Client          Controller      Service         Temporal        Worker          Activities      Database
  │                 │              │               │               │                │              │
  │─POST /orders──→│              │               │               │                │              │
  │                 │              │               │               │                │              │
  │                 │─createOrder→│               │               │                │              │
  │                 │              │               │               │                │              │
  │                 │              │─startWorkflow→│              │                │              │
  │                 │              │               │               │                │              │
  │                 │              │               │─schedule──────→               │              │
  │                 │              │               │               │                │              │
  │                 │              │               │               │─createOrder──→│              │
  │                 │              │               │               │                │              │
  │                 │              │               │               │                │─save────────→│
  │                 │              │               │               │                │              │
  │                 │              │               │               │◄───────────────│◄─────────────│
  │                 │              │               │               │                │              │
  │                 │              │               │               │─authorizePayment→            │
  │                 │              │               │               │                │              │
  │                 │              │               │               │                │─update──────→│
  │                 │              │               │               │                │              │
  │                 │              │               │               │◄───────────────│◄─────────────│
  │                 │              │               │               │                │              │
  │◄──orderId───────│◄─────────────│◄──────────────│              │                │              │
  │                 │              │               │               │                │              │
  │                 │              │               │               │─(continue)─────→              │
  │                 │              │               │               │                │              │
  
  [If failure occurs]
  
  │                 │              │               │               │                │              │
  │                 │              │               │               │─compensate────→│              │
  │                 │              │               │               │                │              │
  │                 │              │               │               │                │─rollback────→│
  │                 │              │               │               │                │              │
```

## Data Flow

```
┌──────────────┐
│ OrderRequest │
│  - orderId   │
│  - customer  │
│  - items[]   │
│  - total     │
└──────┬───────┘
       │
       ▼
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Workflow   │────→│  Activities  │────→│   Database   │
│   (Saga)     │     │  (Business)  │     │   (State)    │
└──────┬───────┘     └──────────────┘     └──────────────┘
       │
       │ generates
       ▼
┌──────────────┐
│ Order Entity │
│  - status    │
│  - paymentId │
│  - reservId  │
│  - shipId    │
│  - tracking  │
└──────────────┘
```

## Technology Stack

```
┌─────────────────────────────────────────────────────────────┐
│                      Application Layer                       │
│  - Spring Boot 3.2.0                                        │
│  - Java 17                                                  │
│  - Maven Build System                                       │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────┐
│                     Orchestration Layer                      │
│  - Temporal Java SDK 1.20.1                                 │
│  - Workflow Definitions                                     │
│  - Activity Definitions                                     │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────┐
│                     Persistence Layer                        │
│  - Spring Data JPA                                          │
│  - Hibernate ORM                                            │
│  - H2 In-Memory Database                                    │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────┐
│                    Infrastructure Layer                      │
│  - Docker & Docker Compose                                  │
│  - Temporal Server (gRPC)                                   │
│  - PostgreSQL (Temporal state)                              │
└─────────────────────────────────────────────────────────────┘
```

## Key Design Patterns

### 1. Saga Pattern
- **Purpose**: Manage distributed transactions
- **Implementation**: OrderWorkflowImpl with compensation logic
- **Benefit**: Ensures data consistency without 2PC

### 2. Activity Pattern
- **Purpose**: Encapsulate business logic
- **Implementation**: OrderActivities interface + implementation
- **Benefit**: Reusable, testable, retriable units of work

### 3. Repository Pattern
- **Purpose**: Abstract data access
- **Implementation**: Spring Data JPA repositories
- **Benefit**: Clean separation of concerns

### 4. Dependency Injection
- **Purpose**: Loose coupling
- **Implementation**: Spring's @Autowired / constructor injection
- **Benefit**: Testability and maintainability

## Scalability Considerations

```
┌─────────────────────────────────────────────────────────────┐
│                     Horizontal Scaling                       │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │   Worker 1   │  │   Worker 2   │  │   Worker N   │     │
│  │  (Instance)  │  │  (Instance)  │  │  (Instance)  │     │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘     │
│         │                  │                  │              │
│         └──────────────────┴──────────────────┘              │
│                            │                                 │
│                    ┌───────▼────────┐                       │
│                    │ Temporal Server │                       │
│                    │  (Task Queue)   │                       │
│                    └─────────────────┘                       │
└─────────────────────────────────────────────────────────────┘

Benefits:
- Multiple workers can process workflows in parallel
- Each worker polls the same task queue
- Temporal ensures each task is processed exactly once
- No coordination needed between workers
```

---

This architecture provides a solid foundation for building resilient, 
distributed transaction management systems using the Saga pattern.
