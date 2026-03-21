package com.engineerneedjob.userservicemanager.service;

import com.engineerneedjob.userservicemanager.dto.request.UserRequest;
import com.engineerneedjob.userservicemanager.dto.response.UserResponse;

public interface UserService {
    UserResponse save(UserRequest userRequest);

    Boolean existByUserId(String userId);
}
