package com.example.ordersaga.service;

import com.example.ordersaga.model.Order;
import com.example.ordersaga.model.OrderRequest;
import com.example.ordersaga.model.OrderStatus;
import com.example.ordersaga.repository.OrderRepository;
import com.example.ordersaga.workflow.OrderWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private static final String ORDER_TASK_QUEUE = "order-processing-queue";
    
    private final WorkflowClient workflowClient;
    private final OrderRepository orderRepository;
    
    public OrderService(WorkflowClient workflowClient, OrderRepository orderRepository) {
        this.workflowClient = workflowClient;
        this.orderRepository = orderRepository;
    }
    
    public String createOrder(OrderRequest orderRequest) {
        // Generate order ID if not provided
        if (orderRequest.getOrderId() == null || orderRequest.getOrderId().isEmpty()) {
            orderRequest.setOrderId("ORD-" + UUID.randomUUID().toString().substring(0, 8));
        }
        
        log.info("Creating order with ID: {}", orderRequest.getOrderId());
        
        // Create workflow options
        WorkflowOptions workflowOptions = WorkflowOptions.newBuilder()
                .setWorkflowId("order-workflow-" + orderRequest.getOrderId())
                .setTaskQueue(ORDER_TASK_QUEUE)
                .build();
        
        // Start the workflow
        OrderWorkflow workflow = workflowClient.newWorkflowStub(
                OrderWorkflow.class, 
                workflowOptions
        );
        
        // Execute workflow asynchronously
        WorkflowClient.start(workflow::processOrder, orderRequest);
        
        log.info("Order workflow started for order: {}", orderRequest.getOrderId());
        
        return orderRequest.getOrderId();
    }
    
    public Order getOrder(String orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }
    
    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
