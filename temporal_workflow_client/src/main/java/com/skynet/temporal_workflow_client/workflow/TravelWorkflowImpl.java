package com.skynet.temporal_workflow_client.workflow;


import com.skynet.temporal_workflow_client.activities.TravelActivities;
import com.skynet.temporal_workflow_client.dto.TravelRequest;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.Workflow;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;


@Slf4j
public class TravelWorkflowImpl implements TravelWorkflow{

    private boolean isUserConfirmed = false;

    @SignalMethod
    public void sendConfirmationSignal() {
        log.info("Received confirmation signal");
        isUserConfirmed = true;
    }

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

        /*
         * 24 hours -> wait for user confirmation if
         * you won't get any within 24 hours then cancel the workflow and rollback it.
         * For Demo purpose we will wait for 2 minutes and not 24 hours
         */
        log.info("Waiting for user confirmation for 2 minutes...");
        boolean isConfirmed = Workflow.await(Duration.ofMinutes(2L), () -> isUserConfirmed);

        if (!isConfirmed) {
            // cancel the booking
            log.info("User did not confirm within 2 minutes, cancelling the booking for user> {}", travelRequest.getUserId());
            travelActivities.cancelBooking(travelRequest);
        } {
            log.info("User did confirm the booking. User: {}", travelRequest.getUserId());
            travelActivities.confirmBooking(travelRequest);
        }

        log.info("Travel booking completed for user: {}", travelRequest.getUserId());
    }

}
