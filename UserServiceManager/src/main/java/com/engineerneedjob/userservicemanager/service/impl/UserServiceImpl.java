package com.engineerneedjob.userservicemanager.service.impl;

import com.engineerneedjob.userservicemanager.dto.mapper.UserMapper;
import com.engineerneedjob.userservicemanager.dto.request.UserRequest;
import com.engineerneedjob.userservicemanager.dto.response.UserResponse;
import com.engineerneedjob.userservicemanager.model.User;
import com.engineerneedjob.userservicemanager.repo.UserRepository;
import com.engineerneedjob.userservicemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse save(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail()))
            return userMapper.toResponse(userRepository.findByEmail(userRequest.getEmail()));

        User user = userMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setKeycloakId(userRequest.getKeycloakId());
        log.info("save user {}", user);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public Boolean existByUserId(String userId) {
        return userRepository.existsByEmail(userId);
    }

    @Override
    public Boolean existByKeyCloakId(String keyCloakId) {
        log.info("keycloakId:{}", keyCloakId);
        return userRepository.existsByKeycloakId(keyCloakId);
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
