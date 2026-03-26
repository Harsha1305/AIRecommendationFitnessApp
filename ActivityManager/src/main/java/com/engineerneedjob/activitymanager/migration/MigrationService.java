package com.engineerneedjob.activitymanager.migration;

import com.engineerneedjob.activitymanager.model.Activity;
import com.engineerneedjob.activitymanager.repo.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigrationService implements ApplicationRunner {

    private final ActivityRepository activityRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Running userId migration check...");

        List<Activity> emailActivities = activityRepository
                .findByUserIdContaining("@");

        if (emailActivities.isEmpty()) {
            log.info("No migration needed. All userIds are already UUIDs.");
            return;
        }

        log.info("Found {} activities with email-based userId. Migrating...",
                emailActivities.size());

        emailActivities.forEach(activity -> {
            log.info("Migrating activity {} from userId '{}' to UUID",
                    activity.getId(), activity.getUserId());
            activity.setUserId("20a862ff-a545-44a0-b5be-18e8a3ff8c47");
            activityRepository.save(activity);
        });

        log.info("Migration complete. {} activities updated.", emailActivities.size());
    }
}
