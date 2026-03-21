package com.engineerneedjob.activitymanager.service;

import com.engineerneedjob.activitymanager.dto.request.ActivityRequest;
import com.engineerneedjob.activitymanager.dto.response.ActivityResponse;

import java.util.List;

public interface ActivityService {
    List<ActivityResponse> syncFromStrava(String userId);

    List<ActivityResponse> getActivities(String userId);

    ActivityResponse logManualActivity(ActivityRequest request);

    ActivityResponse getActivityById(String activityId);
}
