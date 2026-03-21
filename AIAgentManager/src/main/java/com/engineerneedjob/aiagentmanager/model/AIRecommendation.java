package com.engineerneedjob.aiagentmanager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "recommendations")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AIRecommendation {
    @Id
    private String id;
    private String userId;
    private String activityId;
    private ActivityType activityType;
    private String recommendation;          // main AI-generated text
    private List<String> improvements;     // specific improvement tips
    private List<String> suggestions;      // next workout suggestions
    private List<String> safety;           // safety warnings if any

    @CreatedDate
    private LocalDateTime createdAt;
}
