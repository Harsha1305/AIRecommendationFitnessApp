package com.engineerneedjob.activitymanager.controller;

import com.engineerneedjob.activitymanager.dto.request.ActivityRequest;
import com.engineerneedjob.activitymanager.dto.response.ActivityResponse;
import com.engineerneedjob.activitymanager.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
@Slf4j
public class ActivityController {

    private final ActivityService activityService;

    // fetch from Strava and save
    @PostMapping("/sync/{userId}")
    public ResponseEntity<List<ActivityResponse>> syncActivities(@PathVariable String userId) {
        return ResponseEntity.ok(activityService.syncFromStrava(userId));
    }

    // get saved activities from MongoDB
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityResponse>> getActivities(@PathVariable String userId) {
        return ResponseEntity.ok(activityService.getActivities(userId));
    }

    // manual entry fallback (no provider connected)
    @PostMapping("/manual")
    public ResponseEntity<ActivityResponse> logManual(@Validated @RequestBody ActivityRequest request) {
        log.info("Calling User Validation API for userId: {}", request.getUserId());
        return ResponseEntity.ok(activityService.logManualActivity(request));
    }


    @GetMapping("/activity/{activityId}")
    public ResponseEntity<ActivityResponse> getActivity(@PathVariable String activityId){
        return ResponseEntity.ok(activityService.getActivityById(activityId));
    }
}
