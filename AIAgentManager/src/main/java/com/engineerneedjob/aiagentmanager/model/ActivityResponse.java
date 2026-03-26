package com.engineerneedjob.aiagentmanager.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponse {
    @JsonProperty("externalActivityId")
    private String activityId;
    private String userId;
    private ActivityType activityType;
    private IntegrationProvider source;
    private Integer duration;
    private Integer caloriesBurned;
    private Double distance;
    private Integer averageHeartRate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
