package com.engineerneedjob.apigateway.securityconfig;

import com.engineerneedjob.apigateway.userservice.UserRequest;
import com.engineerneedjob.apigateway.userservice.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        UserRequest userRequest = null;
        if (token != null) {
            userRequest = getUserDetails(token);
        }

        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        if (userId == null && userRequest != null) {
            userId = userRequest.getKeycloakId();  // ✅ fixed typo
        }

        if (userId != null && token != null) {
            String finalUserId = userId;
            UserRequest finalUserRequest = userRequest;

            return userService.validateUser(userId)
                    .flatMap(exist -> {
                        if (!exist) {
                            if (finalUserRequest != null) {
                                log.info("Registering new user: {}", finalUserId);
                                return userService.registerUser(finalUserRequest)
                                        .then(Mono.empty());
                            } else {
                                log.warn("User not found but no token claims available");
                                return Mono.empty();
                            }
                        } else {
                            log.info("User already exists: {}", finalUserId);
                            return Mono.empty();
                        }
                    })
                    .then(Mono.defer(() -> {
                        ServerHttpRequest request = exchange.getRequest().mutate()
                                .header("X-User-Id", finalUserId).build();  // ✅ consistent casing
                        return chain.filter(exchange.mutate().request(request).build());
                    }));
        }
        return chain.filter(exchange);
    }

    private UserRequest getUserDetails(String token) {
        try{
            String tokenWithoutBearer = token.replace("Bearer ", "").trim();
            SignedJWT jwt = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = jwt.getJWTClaimsSet();

            UserRequest userRequest = new UserRequest();
            userRequest.setEmail(claims.getStringClaim("email"));
            userRequest.setKeycloakId(claims.getStringClaim("sub"));
            userRequest.setFirstName(claims.getStringClaim("given_name"));
            userRequest.setLastName(claims.getStringClaim("family_name"));
            userRequest.setPassword("dummy@123123");
            String phone = claims.getStringClaim("phone");
            if (phone != null) {
                userRequest.setPhone(phone);
            }
            return userRequest;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
