package com.engineerneedjob.userservicemanager.controller;

import com.engineerneedjob.userservicemanager.dto.request.UserRequest;
import com.engineerneedjob.userservicemanager.dto.response.UserResponse;
import com.engineerneedjob.userservicemanager.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService aInUserService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = aInUserService.save(userRequest);
        if(userResponse==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping("/validate/{userId}")
    public ResponseEntity<Boolean> validateUser(@PathVariable String userId) {
        log.info("Validating userId: {}", userId);

        if (aInUserService.existByUserId(userId))
            return ResponseEntity.ok(true);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }
}
