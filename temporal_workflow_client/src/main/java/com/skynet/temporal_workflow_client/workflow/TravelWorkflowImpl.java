package com.skynet.temporal_workflow_client.workflow;


import com.skynet.temporal_workflow_client.activities.TravelActivities;
import com.skynet.temporal_workflow_client.dto.TravelRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Sl4j
public class TravelWorkflowImpl implements TravelWorkflow{

    @Override
    public void bookTrip(TravelRequest travelRequest) {

        log.info("Starting travel booking for user: {}", travelRequest.getUserId());

        TravelActivities travelActivities = Workflow.newActivityStub(TravelActivities.class,
                  Workflow.ActivityOptions.newBuilder()
                          .setStartToCloseTimeout(Duration.ofSeconds(10))
                          .setRetryOptions(
                                  Workflow.RetryOptions.newBuilder()
                                          .setMaximumAttempts(3)
                                          .build())
                          .build());

        travelActivities.bookFlight(travelRequest);

        travelActivities.bookHotel(travelRequest);

        travelActivities.arrangeTransport(travelRequest);

        log.info("Travel booking completed for user: {}", travelRequest.getUserId());
    }

}
