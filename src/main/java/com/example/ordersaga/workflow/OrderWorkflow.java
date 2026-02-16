package com.example.ordersaga.workflow;

import com.example.ordersaga.model.OrderRequest;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OrderWorkflow {
    
    @WorkflowMethod
    String processOrder(OrderRequest orderRequest);
}
