package com.engineerneedjob.activitymanager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class WebClientConfig {

    //single LoadBalanced builder — used for inter-service calls via Eureka
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .codecs(config -> config
                        .defaultCodecs()
                        .maxInMemorySize(2 * 1024 * 1024));
    }

    //for calling UserService via Eureka load balancing
    @Bean
    public WebClient userServiceWebClient(WebClient.Builder webClientBuilder) {
        log.info("WebClient has been initialized for UserService");
        return webClientBuilder
                .baseUrl("http://USER-SERVICE")  // must match spring.application.name exactly
                .build();
    }

    //for calling Strava external API — NOT load balanced
    @Bean
    public WebClient stravaWebClient() {
        return WebClient.builder()
                .baseUrl("https://www.strava.com/api/v3")
                .codecs(config -> config
                        .defaultCodecs()
                        .maxInMemorySize(2 * 1024 * 1024))
                .build();
    }
}
