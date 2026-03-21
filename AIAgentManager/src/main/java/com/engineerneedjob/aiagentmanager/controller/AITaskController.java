package com.engineerneedjob.aiagentmanager.controller;

import com.engineerneedjob.aiagentmanager.model.AIRecommendation;
import com.engineerneedjob.aiagentmanager.service.AIRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendation")
public class AITaskController {
    private final AIRecommendationService aiRecommendationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AIRecommendation>> getUserRecommendation(@PathVariable String userId) {
        return ResponseEntity.ok(aiRecommendationService.getUserRecommendation(userId));
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<AIRecommendation> getActivityRecommendation(@PathVariable String activityId) {
        return ResponseEntity.ok(aiRecommendationService.getActivityRecommendation(activityId));
    }
}
