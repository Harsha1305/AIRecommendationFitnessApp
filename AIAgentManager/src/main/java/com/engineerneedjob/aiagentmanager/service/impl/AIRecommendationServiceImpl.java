package com.engineerneedjob.aiagentmanager.service.impl;

import com.engineerneedjob.aiagentmanager.model.AIRecommendation;
import com.engineerneedjob.aiagentmanager.repo.RecommendationRepository;
import com.engineerneedjob.aiagentmanager.service.AIRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIRecommendationServiceImpl implements AIRecommendationService {
    @Autowired
    private final RecommendationRepository recommendationRepository;

    public List<AIRecommendation> getUserRecommendation(String userId) {
        return recommendationRepository.findByUserId(userId);
    }

    public AIRecommendation getActivityRecommendation(String activityId) {
        log.info("getActivityRecommendation for {}", activityId);
        return recommendationRepository.findByActivityId(activityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No recommendation found for activityId: " + activityId));
    }
}
