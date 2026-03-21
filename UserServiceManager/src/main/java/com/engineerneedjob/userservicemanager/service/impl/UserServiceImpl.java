package com.engineerneedjob.userservicemanager.service.impl;

import com.engineerneedjob.userservicemanager.dto.mapper.UserMapper;
import com.engineerneedjob.userservicemanager.dto.request.UserRequest;
import com.engineerneedjob.userservicemanager.dto.response.UserResponse;
import com.engineerneedjob.userservicemanager.model.User;
import com.engineerneedjob.userservicemanager.repo.UserRepository;
import com.engineerneedjob.userservicemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse save(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail()))
            throw new IllegalArgumentException("Email already in use");

        User user = userMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public Boolean existByUserId(String userId) {
        return userRepository.existsByEmail(userId);
    }


//    public UserResponse save(UserRequest userRequest) {
//        if(userRequest==null)
//            throw new IllegalArgumentException("userRequest cannot be null");
//        User aInUser = new User();
//        aInUser.setEmail(userRequest.getEmail());
//        aInUser.setPassword(userRequest.getPassword());
//        aInUser.setFirstName(userRequest.getFirstName());
//        aInUser.setLastName(userRequest.getLastName());
//        aInUser.setPhone(userRequest.getPhone());
//        User aOutUser = userRepository.save(aInUser);
//        if(aOutUser==null)
//            throw new IllegalArgumentException("user could not be saved");
//        UserResponse userResponse = new UserResponse();
//        userResponse.setEmail(aOutUser.getEmail());
//        userResponse.setPhone(aOutUser.getPhone());
//        userResponse.setFirstName(aOutUser.getFirstName());
//        userResponse.setLastName(aOutUser.getLastName());
//        userResponse.setCreated(aOutUser.getCreated());
//        userResponse.setUpdated(aOutUser.getUpdated());
//        return userResponse;
//    }
}
