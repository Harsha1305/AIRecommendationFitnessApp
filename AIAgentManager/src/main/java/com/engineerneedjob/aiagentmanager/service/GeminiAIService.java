package com.engineerneedjob.aiagentmanager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiAIService {

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String geminiModel;

    public GeminiAIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    public String getAnswer(String question) {
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", question)
                        ))
                )
        );

        try {
            String response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/models/{model}:generateContent")
                            .queryParam("key", geminiApiKey)
                            .build(geminiModel))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Gemini call succeeded for model={}", geminiModel);
            return response;

        } catch (WebClientResponseException e) {
            log.error("Gemini API failed. status={}, body={}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    e);
            throw new RuntimeException("Failed to call Gemini API: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Failed to call Gemini API", e);
            throw new RuntimeException("Failed to call Gemini API", e);
        }
    }
}