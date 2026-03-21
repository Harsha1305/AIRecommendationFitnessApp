package com.engineerneedjob.aiagentmanager.service;

import com.engineerneedjob.aiagentmanager.model.AIRecommendation;

import java.util.List;

public interface AIRecommendationService {
    AIRecommendation getActivityRecommendation(String activityId);

    List<AIRecommendation> getUserRecommendation(String userId);
}
