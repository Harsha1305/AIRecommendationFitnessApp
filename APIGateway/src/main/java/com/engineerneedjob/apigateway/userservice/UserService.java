package com.engineerneedjob.apigateway.userservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId) {
        return userServiceWebClient.get()
                .uri("/api/users/validate/" + userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(e -> {
                    log.warn("User not found in user-service, will attempt registration: {}", userId);
                    return Mono.just(false);  // ← return false, not an exception
                });
    }

    public Mono<Void> registerUser(UserRequest userRequest) {
        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(userRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> {
                    log.warn("Registration failed, continuing anyway: {}", e.getMessage());
                    return Mono.empty();  // ← don't throw, just log and proceed
                });
    }
}