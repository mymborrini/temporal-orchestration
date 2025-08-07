package com.skynet.temporal_workflow_client.activities;

import com.skynet.temporal_workflow_client.dto.TravelRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TravelActivitiesImpl implements TravelActivities {


    /**
     * In this case this will not work because this activity, even if is a bean is not managed by spring directly but using
     * temporal so the solutions could be two. Using a constructor value
     */
    @Value("${temporal_workflow_client.activities.exceptions.arrangeTransport}")
    private boolean exceptionsArrangeTransport;


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

        log.info("Exception in arranging transport: {}", exceptionsArrangeTransport);
        // Since we run this exception temporal will automatically retry this activity for us
        if (exceptionsArrangeTransport){
            throw  new RuntimeException("Simulated transport arranged failure!");
        }
    }

    @Override
    public void cancelBooking(TravelRequest travelRequest) {
        log.info("Cancelling booking for user: {} to destination: {} on date {}",
                 travelRequest.getUserId(),
                 travelRequest.getDestination(),
                 travelRequest.getTravelDate()
        );
    }

    @Override
    public void confirmBooking(TravelRequest travelRequest) {
        log.info("Confirming booking for user: {} to destination: {} on date {}",
                 travelRequest.getUserId(),
                 travelRequest.getDestination(),
                 travelRequest.getTravelDate()
        );
    }
}
