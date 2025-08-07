package com.skynet.temporal_workflow_client.activities;

import com.skynet.temporal_workflow_client.dto.TravelRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TravelActivitiesImpl implements TravelActivities {


    @Override
    public void bookFlight(TravelRequest travelRequest) {
        // Rest call to flight microservice
        log.info("Flight booked for user: {} to destination: {} on date {}",
            travelRequest.getUserId(),
            travelRequest.getDestination(),
            travelRequest.getTravelDate()
        );
    }

    @Override
    public void bookHotel(TravelRequest travelRequest) {
        // grpc call to hotel service
        log.info("Hotel booked for user: {} to destination: {} on date {}",
                travelRequest.getUserId(),
                travelRequest.getDestination(),
                travelRequest.getTravelDate()
        );
    }

    @Override
    public void arrangeTransport(TravelRequest travelRequest) {
        // kafka message to transport service
        log.info("Transport arranged for user: {} to destination: {} on date {}",
                travelRequest.getUserId(),
                travelRequest.getDestination(),
                travelRequest.getTravelDate()
        );
    }
}
