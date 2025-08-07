package com.skynet.temporal_workflow_client.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravelRequest {

    private String userId;
    private String destination;
    private String travelDate;
}
