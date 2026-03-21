package com.engineerneedjob.activitymanager.service.impl;

import com.engineerneedjob.activitymanager.dto.mapper.ActivityMapper;
import com.engineerneedjob.activitymanager.dto.request.ActivityRequest;
import com.engineerneedjob.activitymanager.dto.request.StravaActivity;
import com.engineerneedjob.activitymanager.dto.response.ActivityResponse;
import com.engineerneedjob.activitymanager.model.Activity;
import com.engineerneedjob.activitymanager.model.UserIntegration;
import com.engineerneedjob.activitymanager.repo.ActivityRepository;
import com.engineerneedjob.activitymanager.repo.UserIntegrationRepository;
import com.engineerneedjob.activitymanager.service.ActivityService;
import com.engineerneedjob.activitymanager.service.StravaService;
import com.engineerneedjob.activitymanager.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final UserIntegrationRepository integrationRepository;
    private final StravaService stravaService;
    private final ActivityMapper activityMapper;
    private final UserValidationService userValidationService;
    private final KafkaTemplate<String, ActivityResponse> kafkaTemplate;

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    @Override
    public List<ActivityResponse> syncFromStrava(String userId) {
        // 1. get user's strava token
        UserIntegration integration = integrationRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Strava not connected for user: " + userId));

        // 2. fetch from Strava — calories already included
        List<StravaActivity> stravaActivities = stravaService.getActivities(
                integration.getStravaAccessToken()
        );

        // 3. map and save — no calculation needed
        List<Activity> activities = stravaActivities.stream()
                .filter(sa -> !activityRepository.existsByExternalActivityId(sa.getId())) // avoid duplicates
                .map(sa -> activityMapper.fromStrava(sa, userId))
                .collect(Collectors.toList());

        activityRepository.saveAll(activities);

        return activities.stream()
                .map(activityMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityResponse> getActivities(String userId) {
        // just fetch from MongoDB and return
        return activityRepository.findByUserId(userId)
                .stream()
                .map(activityMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ActivityResponse logManualActivity(ActivityRequest request) {
        log.info("Calling ActivityService API for userId: {}", request.getUserId());
        // only here do you calculate calories manually
        boolean isValidUser = userValidationService.validateUser(request.getUserId());
        if (!isValidUser) {
            log.error("Invalid user ID: " + request.getUserId());
            throw new RuntimeException("Invalid User: " + request.getUserId());
        }
        Activity activity = activityMapper.fromRequest(request);
        ActivityResponse aOutActivityResponse = activityMapper.
                toResponse(activityRepository.save(activity));
        try{
            kafkaTemplate.send(topicName,request.getUserId(), aOutActivityResponse);
        } catch (Exception e) {
            log.error("Exception occurred while sending activity to AI Manager", e);
            throw new RuntimeException(e);
        }

        return aOutActivityResponse;
    }

    @Override
    public ActivityResponse getActivityById(String activityId) {
        Activity aOutActivity = activityRepository.findById(activityId).
                orElseThrow(() -> new RuntimeException("Activity does not exist with {}" + activityId));

        return activityMapper.toResponse(aOutActivity);
    }
}
