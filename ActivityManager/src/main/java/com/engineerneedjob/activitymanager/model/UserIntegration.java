package com.engineerneedjob.activitymanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "user_integrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIntegration {
    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private String stravaAccessToken;
    private String stravaRefreshToken;
    private LocalDateTime stravaTokenExpiry;

    private String googleFitAccessToken;
    private String googleFitRefreshToken;

    private IntegrationProvider activeProvider; // STRAVA, GOOGLE_FIT, MANUAL
}
