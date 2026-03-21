package com.engineerneedjob.activitymanager.repo;

import com.engineerneedjob.activitymanager.model.UserIntegration;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserIntegrationRepository extends MongoRepository<UserIntegration, String> {
    Optional<UserIntegration> findByUserId(String userId);
}
