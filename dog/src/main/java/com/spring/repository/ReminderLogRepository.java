package com.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.domain.ReminderLog;

@Repository
public interface ReminderLogRepository extends JpaRepository<ReminderLog, Long> {
	
}
