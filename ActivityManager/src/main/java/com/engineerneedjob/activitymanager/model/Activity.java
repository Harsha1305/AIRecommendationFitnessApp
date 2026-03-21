package com.engineerneedjob.activitymanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "activity")
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
    @Id
    private String id;

    @Indexed
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

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
