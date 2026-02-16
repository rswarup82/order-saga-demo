package com.example.ordersaga.workflow;

import com.example.ordersaga.activities.OrderActivities;
import com.example.ordersaga.model.*;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class OrderWorkflowImpl implements OrderWorkflow {
    
    private static final Logger logger = Workflow.getLogger(OrderWorkflowImpl.class);
    
    private final ActivityOptions activityOptions = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(5))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumAttempts(3)
                    .setInitialInterval(Duration.ofSeconds(1))
                    .setMaximumInterval(Duration.ofSeconds(10))
                    .setBackoffCoefficient(2.0)
                    .build())
            .build();
    
    private final OrderActivities activities = Workflow.newActivityStub(
            OrderActivities.class, 
            activityOptions
    );
    
    @Override
    public String processOrder(OrderRequest orderRequest) {
        logger.info("Starting order workflow for order: {}", orderRequest.getOrderId());
        
        // Initialize Saga for compensation handling
        Saga saga = new Saga(new Saga.Options.Builder().setParallelCompensation(false).build());
        
        try {
            // Step 1: Create Order
            logger.info("Step 1: Creating order {}", orderRequest.getOrderId());
            activities.createOrder(orderRequest);
            saga.addCompensation(() -> activities.markOrderAsFailed(
                    orderRequest.getOrderId(), 
                    "Order creation compensation"
            ));
            
            // Step 2: Authorize Payment
            logger.info("Step 2: Authorizing payment for order {}", orderRequest.getOrderId());
            PaymentResult paymentResult = activities.authorizePayment(orderRequest);
            
            if (!paymentResult.isSuccess()) {
                throw new RuntimeException("Payment authorization failed: " + paymentResult.getMessage());
            }
            
            saga.addCompensation(() -> activities.compensatePayment(
                    orderRequest.getOrderId(), 
                    paymentResult.getPaymentId()
            ));
            
            // Step 3: Reserve Inventory
            logger.info("Step 3: Reserving inventory for order {}", orderRequest.getOrderId());
            InventoryResult inventoryResult = activities.reserveInventory(orderRequest);
            
            if (!inventoryResult.isSuccess()) {
                throw new RuntimeException("Inventory reservation failed: " + inventoryResult.getMessage());
            }
            
            saga.addCompensation(() -> activities.compensateInventory(
                    orderRequest.getOrderId(), 
                    inventoryResult.getReservationId()
            ));
            
            // Step 4: Perform Fraud Check
            logger.info("Step 4: Performing fraud check for order {}", orderRequest.getOrderId());
            FraudCheckResult fraudCheckResult = activities.performFraudCheck(orderRequest);
            
            if (!fraudCheckResult.isPassed()) {
                throw new RuntimeException("Fraud check failed: " + fraudCheckResult.getMessage());
            }
            
            // Step 5: Confirm Order
            logger.info("Step 5: Confirming order {}", orderRequest.getOrderId());
            activities.confirmOrder(orderRequest.getOrderId());
            
            // Step 6: Arrange Shipping
            logger.info("Step 6: Arranging shipping for order {}", orderRequest.getOrderId());
            ShippingResult shippingResult = activities.arrangeShipping(orderRequest);
            
            if (!shippingResult.isSuccess()) {
                throw new RuntimeException("Shipping arrangement failed: " + shippingResult.getMessage());
            }
            
            saga.addCompensation(() -> activities.compensateShipping(
                    orderRequest.getOrderId(), 
                    shippingResult.getShippingId()
            ));
            
            // Step 7: Update Delivery Tracking
            logger.info("Step 7: Updating delivery tracking for order {}", orderRequest.getOrderId());
            activities.updateDeliveryTracking(
                    orderRequest.getOrderId(), 
                    shippingResult.getTrackingNumber()
            );
            
            // Step 8: Complete Order
            logger.info("Step 8: Completing order {}", orderRequest.getOrderId());
            activities.completeOrder(orderRequest.getOrderId());
            
            logger.info("Order workflow completed successfully for order: {}", orderRequest.getOrderId());
            return "Order processed successfully: " + orderRequest.getOrderId();
            
        } catch (Exception e) {
            logger.error("Order workflow failed for order: {}. Error: {}", 
                    orderRequest.getOrderId(), e.getMessage());
            
            // Trigger saga compensation
            logger.info("Starting compensation for order: {}", orderRequest.getOrderId());
            saga.compensate();
            
            // Mark order as failed
            activities.markOrderAsFailed(orderRequest.getOrderId(), e.getMessage());
            activities.updateOrderStatus(orderRequest.getOrderId(), OrderStatus.COMPENSATED);
            
            logger.info("Compensation completed for order: {}", orderRequest.getOrderId());
            
            throw new RuntimeException("Order processing failed: " + e.getMessage(), e);
        }
    }
}
