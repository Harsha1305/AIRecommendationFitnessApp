package com.engineerneedjob.userservicemanager.dto.mapper;

import com.engineerneedjob.userservicemanager.dto.request.UserRequest;
import com.engineerneedjob.userservicemanager.dto.response.UserResponse;
import com.engineerneedjob.userservicemanager.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
    User toEntity(UserRequest request);
}
