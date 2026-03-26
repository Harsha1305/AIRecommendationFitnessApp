package com.engineerneedjob.activitymanager.dto.response;

import com.engineerneedjob.activitymanager.model.ActivityType;
import com.engineerneedjob.activitymanager.model.IntegrationProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityResponse {
    private String id;                // ← add this
    private String userId;
    private ActivityType activityType;
    private IntegrationProvider source;
    private Integer duration;
    private Integer caloriesBurned;
    private Double distance;
    private Integer averageHeartRate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String externalActivityId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
