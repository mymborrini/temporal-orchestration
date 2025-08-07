package com.skynet.temporal_workflow_client.controller;

import com.skynet.temporal_workflow_client.dto.TravelRequest;
import com.skynet.temporal_workflow_client.starter.TravelBookingWorkflowStarter;
import com.skynet.temporal_workflow_client.workflow.TravelWorkflow;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/travel")
@RequiredArgsConstructor
public class TravelWorkflowController {

    private final TravelBookingWorkflowStarter workflowStarter;


    @PostMapping("/book")
    public ResponseEntity<String> bookTravel(@RequestBody TravelRequest travelRequest){
        workflowStarter.startWorkflow(travelRequest);
        return ResponseEntity.ok("Travel booking workflow started for user: " + travelRequest.getUserId());
    }

    @PostMapping("/confirm/{userId}")
    public ResponseEntity<String> confirmTravel(@PathVariable String userId){
        workflowStarter.sendConfirmationSignal(userId);
        return ResponseEntity.ok("Travel confirmation requested by user: " + userId);
    }

}
