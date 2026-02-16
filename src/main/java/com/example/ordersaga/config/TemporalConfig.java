package com.example.ordersaga.config;

import com.example.ordersaga.activities.OrderActivitiesImpl;
import com.example.ordersaga.workflow.OrderWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {
    
    private static final Logger log = LoggerFactory.getLogger(TemporalConfig.class);
    
    private final OrderActivitiesImpl orderActivities;
    
    @Value("${temporal.service.url:localhost:7233}")
    private String temporalServiceUrl;
    
    @Value("${temporal.namespace:default}")
    private String namespace;
    
    @Value("${temporal.task-queue:order-processing-queue}")
    private String taskQueue;
    
    private WorkerFactory workerFactory;
    
    public TemporalConfig(OrderActivitiesImpl orderActivities) {
        this.orderActivities = orderActivities;
    }
    
    @Bean
    public WorkflowServiceStubs workflowServiceStubs() {
        log.info("Connecting to Temporal service at: {}", temporalServiceUrl);
        
        WorkflowServiceStubsOptions options = WorkflowServiceStubsOptions.newBuilder()
                .setTarget(temporalServiceUrl)
                .build();
        
        return WorkflowServiceStubs.newServiceStubs(options);
    }
    
    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs workflowServiceStubs) {
        log.info("Creating Temporal WorkflowClient for namespace: {}", namespace);
        
        WorkflowClientOptions clientOptions = WorkflowClientOptions.newBuilder()
                .setNamespace(namespace)
                .build();
        
        return WorkflowClient.newInstance(workflowServiceStubs, clientOptions);
    }
    
    @Bean
    public WorkerFactory workerFactory(WorkflowClient workflowClient) {
        log.info("Creating Temporal WorkerFactory");
        return WorkerFactory.newInstance(workflowClient);
    }
    
    @PostConstruct
    public void startWorker() {
        log.info("Starting Temporal worker on task queue: {}", taskQueue);
        
        WorkerFactory factory = workerFactory(workflowClient(workflowServiceStubs()));
        Worker worker = factory.newWorker(taskQueue);
        
        // Register workflow implementations
        worker.registerWorkflowImplementationTypes(OrderWorkflowImpl.class);
        
        // Register activity implementations
        worker.registerActivitiesImplementations(orderActivities);
        
        // Start the worker
        factory.start();
        this.workerFactory = factory;
        
        log.info("Temporal worker started successfully");
    }
    
    @PreDestroy
    public void shutdown() {
        if (workerFactory != null) {
            log.info("Shutting down Temporal worker");
            workerFactory.shutdown();
        }
    }
}
