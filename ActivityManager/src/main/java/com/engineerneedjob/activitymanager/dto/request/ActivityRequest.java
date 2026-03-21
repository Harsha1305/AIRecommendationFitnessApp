package com.engineerneedjob.activitymanager.dto.request;


import com.engineerneedjob.activitymanager.model.ActivityType;
import com.engineerneedjob.activitymanager.model.IntegrationProvider;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ActivityRequest {
    private String userId;

    @NotNull(message = "Activity type is required")
    private ActivityType activityType;          // ✅ GYM, CYCLE, WALK etc.

    @NotNull(message = "Source is required")
    private IntegrationProvider source;         // ✅ STRAVA, GOOGLE_FIT, MANUAL

    @Positive(message = "Duration must be positive")
    private Integer duration;                   // in minutes

    private Integer caloriesBurned;             // optional — null if manual and no calc yet

    private Double distance;                    // nullable — only for running/cycling

    private Integer averageHeartRate;           // nullable — only if device provides it

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    private LocalDateTime endTime;              // nullable — user might still be active

    private String externalActivityId;          // nullable — only for Strava/GoogleFit
}