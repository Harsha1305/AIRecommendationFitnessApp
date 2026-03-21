package com.engineerneedjob.activitymanager.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // ✅ ignore extra fields Strava sends
public class StravaActivity {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;                    // "Run", "Ride", "Swim" etc.

    @JsonProperty("distance")
    private Double distance;                // in meters

    @JsonProperty("moving_time")
    private Integer movingTime;             // in seconds

    @JsonProperty("elapsed_time")
    private Integer elapsedTime;            // in seconds

    @JsonProperty("total_elevation_gain")
    private Double totalElevationGain;

    @JsonProperty("calories")
    private Integer calories;               // ✅ already calculated by Strava

    @JsonProperty("average_speed")
    private Double averageSpeed;            // in meters per second

    @JsonProperty("average_heartrate")
    private Double averageHeartRate;

    @JsonProperty("start_date")
    private String startDate;
}
