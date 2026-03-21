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
    private String userId;

    private ActivityType activityType;
    private IntegrationProvider source; //STRAVA, GOOGLE_FIT, or MANUAL

    private Integer duration;           // in minutes
    private Integer caloriesBurned;     // from provider or calculated

    private Double distance;            //add for running/cycling
    private Integer averageHeartRate;   //add for gym/running

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String externalActivityId;  //prevents duplicate imports from Strava

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
