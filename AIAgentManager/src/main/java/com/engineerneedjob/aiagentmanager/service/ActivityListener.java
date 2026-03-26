package com.engineerneedjob.aiagentmanager.service;

import com.engineerneedjob.aiagentmanager.model.AIRecommendation;
import com.engineerneedjob.aiagentmanager.model.ActivityResponse;
import com.engineerneedjob.aiagentmanager.repo.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityListener {

    private final ActivityAIService aiService;
    private final RecommendationRepository recommendationRepository;
//    @Value("${spring.kafka.topic.name}")
//    private String topicName;


//    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "ai-agent-group")
//    public void processActivityListener(ActivityResponse activityResponse) {
//        log.info("Received activity for processing: {}", activityResponse.getUserId());
//        AIRecommendation recommendation = aiService.generateRecommendation(activityResponse);
//        recommendationRepository.save(recommendation);
//    }

    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "ai-agent-group")
    public void processActivityListener(ActivityResponse activityResponse) {
        try {
            log.info("Received activity for processing: userId={}, activityId={}",
                    activityResponse.getUserId(),
                    activityResponse.getActivityId());

            AIRecommendation recommendation = aiService.generateRecommendation(activityResponse);
            recommendationRepository.save(recommendation);

            log.info("Recommendation saved successfully for activityId={}",
                    activityResponse.getActivityId());
        } catch (Exception e) {
            log.error("Failed to process activity: userId={}, activityId={}",
                    activityResponse.getUserId(),
                    activityResponse.getActivityId(),
                    e);
        }
    }
}
