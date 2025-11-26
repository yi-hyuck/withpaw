package com.mysite.test.schedule;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleReminderService {

	private final ScheduleInstanceRepository instanceRepository;
	private final ReminderLogRepository reminderLogRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final NotificationSettingService notificationSettingService;
    private final FcmService fcmService;   

	@Scheduled(fixedRate = 60000) // 1분마다 실행
	public void checkUpcomingReminders() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime in1hour = now.plusHours(1);

		// 1시간 내 일정만 조회 (리마인더 기준 탐색용)
		List<ScheduleInstance> instances = instanceRepository.findWithScheduleByOccurrenceTimeBetween(now, in1hour);
		log.info("[리마인더 점검] 조회된 일정 수: {}", instances.size());

		for (ScheduleInstance instance : instances) {
			var schedule = instance.getSchedule();
			if (schedule == null)
				continue;
			Integer remindBefore = schedule.getRemindBeforeMinutes();
			if (remindBefore == null || remindBefore <= 0)
				continue;

			LocalDateTime targetTime = instance.getOccurrenceTime().minusMinutes(remindBefore);

			// targetTime이 현재 시간 기준으로 1분 내인 경우
            if (targetTime.isBefore(now) || targetTime.isBefore(now.plusMinutes(1))) continue;

            
            if (reminderLogRepository.existsByScheduleInstanceIdAndReminderTimeBetween(
                    instance.getId(), targetTime, targetTime.plusMinutes(1))) {
                log.info("[리마인더 스킵] 이미 발송됨 instanceId={}, targetTime={}", instance.getId(), targetTime);
                continue;
            }
            
            if (!notificationSettingService.isPushAllowed(schedule.getMemberId())) {
                log.info("[리마인더 스킵] 푸시 거부 memberId={}", schedule.getMemberId());
                continue;
            }
            
            
            
            String title = schedule.getTitle();
            String msg  = "🔔 " + remindBefore + "분 후에 [" + title + "] 일정입니다. (" + instance.getOccurrenceTime() + ")";

            // 회원의 모든 기기로 발송
            var tokens = deviceTokenRepository.findAllByMemberIdAndEnabledTrue(schedule.getMemberId());

            if (tokens.isEmpty()) {
                log.info("[리마인더 스킵] 토큰 없음 memberId={}", schedule.getMemberId());
                continue;
            }
            
            int success = 0, fail = 0;
            for (var dt : tokens) {
                try {
                    fcmService.sendToToken(dt.getToken(), "알림", msg);
                    success++;
                } catch (Exception ex) {
                    fail++;
                    log.warn("[FCM 실패] memberId={}, tokenId={}, cause={}", schedule.getMemberId(), dt.getId(), ex.getMessage());
                }
            }

				reminderLogRepository.save(ReminderLog.builder().scheduleInstance(instance).reminderTime(now)
						.message(msg).success(success > 0 && fail == 0).build());
	            log.info("[리마인더 발송] memberId={}, title={}, success={}, fail={}",
	                    schedule.getMemberId(), title, success, fail);


				
			}
		}
	}

