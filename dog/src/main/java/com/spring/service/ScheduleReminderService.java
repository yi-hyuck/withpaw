package com.spring.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.spring.domain.ReminderLog;
import com.spring.domain.ScheduleInstance;
import com.spring.repository.ReminderLogRepository;
import com.spring.repository.ScheduleInstanceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleReminderService {

	private final ScheduleInstanceRepository instanceRepository;
	private final ReminderLogRepository reminderLogRepository;

	@Scheduled(fixedRate = 60000) // 1분마다 실행
	public void checkUpcomingReminders() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime in1hour = now.plusHours(1);

		// 1시간 내 일정만 조회 (리마인더 기준 탐색용)
		List<ScheduleInstance> instances = instanceRepository.findWithScheduleByOccurrenceTimeBetween(now, in1hour);

		for (ScheduleInstance instance : instances) {
			if (instance.getSchedule() == null)
				continue;
			Integer remindBefore = instance.getSchedule().getRemindBeforeMinutes();
			if (remindBefore == null || remindBefore <= 0)
				continue;

			LocalDateTime targetTime = instance.getOccurrenceTime().minusMinutes(remindBefore);

			// targetTime이 현재 시간 기준으로 1분 내인 경우
			if (!targetTime.isAfter(now) && targetTime.isAfter(now.minusMinutes(1))) {
				String title = instance.getSchedule().getTitle();
				String msg = "🔔" + title + " 일정이 " + remindBefore + "분 후입니다! (" + instance.getOccurrenceTime() + ")";
				log.info(msg);

				reminderLogRepository.save(ReminderLog.builder().scheduleInstance(instance).reminderTime(now)
						.message(msg).success(true).build());
			}
		}
	}
}
