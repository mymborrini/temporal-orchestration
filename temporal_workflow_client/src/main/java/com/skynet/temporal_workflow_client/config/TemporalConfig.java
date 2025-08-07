package com.skynet.temporal_workflow_client.config;

import com.skynet.temporal_workflow_client.activities.TravelActivitiesImpl;
import com.skynet.temporal_workflow_client.workflow.TravelWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {

  /**
   * @param serviceStubs Temporal service stubs for communication with the Temporal server.
   *                      To see stub in grpc client check the grpc-api project
   */

  @Bean
  public WorkerFactory workerFactory(WorkflowServiceStubs serviceStubs) {
    WorkflowClient client = WorkflowClient.newInstance(serviceStubs);
    WorkerFactory factory = WorkerFactory.newInstance(client);

    // The worker factory needs to be listen to a particular queue
    Worker worker = factory.newWorker("TRAVEL_TASK_QUEUE");
    worker.registerWorkflowImplementationTypes(TravelWorkflowImpl.class);
    worker.registerActivitiesImplementations(new TravelActivitiesImpl());

    return factory;
  }

  @Bean
  public WorkflowServiceStubs serviceStubs(){
    return WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions.newBuilder()
                                                        .setTarget("127.0.0.1:45000")
                                                        .build());
  }


}
