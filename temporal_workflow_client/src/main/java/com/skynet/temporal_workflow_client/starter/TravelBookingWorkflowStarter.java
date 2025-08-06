package com.skynet.temporal_workflow_client.starter;

import com.skynet.temporal_workflow_client.dto.TravelRequest;
import com.skynet.temporal_workflow_client.workflow.TravelWorkflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TravelBookingWorkflowStarter {

   private final WorkflowServiceStubs serviceStubs;

   public void startWorkflow(TravelRequest travelRequest) {

       TravelWorkflow workflow = client.newWorkflowStub(
               TravelWorkflow.class,
               WorkflowOptions.newBuilder()
                       .setTaskQueue("TRAVEL_TASK_QUEUE")
                       .setWorkflowId("travel_" + travelRequest.getUserId())
                       .build());

       WorkflowClient.start(workflow::bookTrip, travelRequest);
   }


}
