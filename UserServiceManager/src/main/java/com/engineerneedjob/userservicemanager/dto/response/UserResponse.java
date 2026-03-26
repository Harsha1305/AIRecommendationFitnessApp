package com.engineerneedjob.userservicemanager.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private String email;
    private String keycloakId;
    private String phone;
    private String firstName;
    private String lastName;
    private LocalDateTime created;
    private LocalDateTime updated;
}
