package com.engineerneedjob.aiagentmanager.service.impl;

import com.engineerneedjob.aiagentmanager.model.AIRecommendation;
import com.engineerneedjob.aiagentmanager.repo.RecommendationRepository;
import com.engineerneedjob.aiagentmanager.service.AIRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIRecommendationServiceImpl implements AIRecommendationService {
    @Autowired
    private final RecommendationRepository recommendationRepository;

    public List<AIRecommendation> getUserRecommendation(String userId) {
        return recommendationRepository.findByUserId(userId);
    }

    public AIRecommendation getActivityRecommendation(String activityId) {
        return recommendationRepository.findByActivityId(activityId)
                .orElseThrow(() -> new RuntimeException("No recommendation found for this activity: " + activityId));
    }
}
