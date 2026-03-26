package com.engineerneedjob.activitymanager.dto.mapper;

import com.engineerneedjob.activitymanager.dto.request.ActivityRequest;
import com.engineerneedjob.activitymanager.dto.request.StravaActivity;
import com.engineerneedjob.activitymanager.dto.response.ActivityResponse;
import com.engineerneedjob.activitymanager.model.Activity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "externalActivityId", source = "id")
    ActivityResponse toResponse(Activity activity);

    Activity fromRequest(ActivityRequest request);

    @Mapping(target = "externalActivityId", source = "stravaActivity.id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "caloriesBurned", source = "stravaActivity.calories")
    @Mapping(target = "distance", source = "stravaActivity.distance")
    @Mapping(target = "duration", source = "stravaActivity.movingTime")
    @Mapping(target = "source", expression = "java(com.engineerneedjob.activitymanager.model.IntegrationProvider.STRAVA)") // ✅ fixed
    @Mapping(target = "activityType", ignore = true)    // ✅ Strava uses String type, map manually if needed
    @Mapping(target = "averageHeartRate", source = "stravaActivity.averageHeartRate")
    @Mapping(target = "startTime", ignore = true)       // ✅ needs custom conversion from String to LocalDateTime
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "createdAt", ignore = true)       // ✅ server generated
    @Mapping(target = "updatedAt", ignore = true)       // ✅ server generated
    Activity fromStrava(StravaActivity stravaActivity, String userId);
}
