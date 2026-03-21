package com.engineerneedjob.activitymanager.service;

import com.engineerneedjob.activitymanager.dto.request.StravaActivity;
import com.engineerneedjob.activitymanager.dto.request.StravaTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StravaService {

    private final WebClient stravaWebClient;

    // fetch all activities for a user
    public List<StravaActivity> getActivities(String accessToken) {
        return stravaWebClient.get()
                .uri("https://www.strava.com/api/v3/athlete/activities")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToFlux(StravaActivity.class)  // deserialize JSON array → StravaActivity
                .collectList()
                .block();
    }

    // fetch a single activity by Strava activity ID
    public StravaActivity getActivityById(String accessToken, String activityId) {
        return stravaWebClient.get()
                .uri("https://www.strava.com/api/v3/activities/" + activityId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(StravaActivity.class)
                .block();
    }

    // refresh expired access token using refresh token
    public StravaTokenResponse refreshToken(String refreshToken) {
        return stravaWebClient.post()
                .uri("https://www.strava.com/oauth/token")
                .bodyValue(Map.of(
                        "client_id",     "${strava.client-id}",
                        "client_secret", "${strava.client-secret}",
                        "refresh_token", refreshToken,
                        "grant_type",    "refresh_token"
                ))
                .retrieve()
                .bodyToMono(StravaTokenResponse.class)
                .block();
    }
}
