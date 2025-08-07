package com.skynet.temporal_workflow_client.workflow;


import com.skynet.temporal_workflow_client.activities.TravelActivities;
import com.skynet.temporal_workflow_client.dto.TravelRequest;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Service
@Slf4j
public class TravelWorkflowImpl implements TravelWorkflow{

    @Override
    public void bookTrip(TravelRequest travelRequest) {

        log.info("Starting travel booking for user: {}", travelRequest.getUserId());

        TravelActivities travelActivities = Workflow.newActivityStub(TravelActivities.class,
                                                                     ActivityOptions.newBuilder()
                          .setStartToCloseTimeout(Duration.ofSeconds(10))
                          .setRetryOptions(
                                  RetryOptions.newBuilder()
                                          .setMaximumAttempts(3)
                                          .build())
                          .build());

        travelActivities.bookFlight(travelRequest);

        travelActivities.bookHotel(travelRequest);

        travelActivities.arrangeTransport(travelRequest);

        log.info("Travel booking completed for user: {}", travelRequest.getUserId());
    }

}
