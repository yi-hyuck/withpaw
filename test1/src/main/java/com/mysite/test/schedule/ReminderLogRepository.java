package com.mysite.test.schedule;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderLogRepository extends JpaRepository<ReminderLog, Long> {
    boolean existsByScheduleInstanceIdAndReminderTimeBetween(
            Long scheduleInstanceId,
            LocalDateTime start,
            LocalDateTime end
    );
}
