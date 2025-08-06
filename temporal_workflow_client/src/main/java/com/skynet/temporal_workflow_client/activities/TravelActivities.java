package com.skynet.temporal_workflow_client.activities;

import com.skynet.temporal_workflow_client.dto.TravelRequest;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface TravelActivities {

    void bookFlight(TravelRequest travelRequest);
    void bookHotel(TravelRequest travelRequest);
    void arrangeTransport(TravelRequest travelRequest);
}
