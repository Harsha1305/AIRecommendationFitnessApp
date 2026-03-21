package com.engineerneedjob.activitymanager.repo;

import com.engineerneedjob.activitymanager.dto.response.ActivityResponse;
import com.engineerneedjob.activitymanager.model.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends MongoRepository<Activity, String> {
    boolean existsByExternalActivityId(String id);

    List<Activity> findByUserId(String userId);

    Optional<Activity> findById(String activityId);
}
