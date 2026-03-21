package com.engineerneedjob.activitymanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Metrics {
    @Id
    private String id;

    private String userId;
    private ActivityType activityType;

    // ✅ raw data from provider
    private Double distance;            // in meters
    private Integer duration;           // in seconds
    private Integer caloriesBurned;     // provided directly by Strava/GoogleFit
    private Double averageSpeed;
    private Integer averageHeartRate;
    private Double elevationGain;

    // ✅ fallback if no provider connected
    private Double caloriesPerMinute;

    private IntegrationProvider source; // where did this data come from?
    private String externalActivityId;  // Strava/GoogleFit activity ID for deduplication

    private LocalDateTime recordedAt;
}
