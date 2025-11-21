package com.mysite.test.schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.test.exception.BadRequestException;
import com.mysite.test.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleInstanceService {

	private final ScheduleRepository scheduleRepository;
	private final ScheduleInstanceRepository instanceRepository;
	private final ScheduleService scheduleService;

	// 특정 일정의 발생 인스턴스 생성
	public List<ScheduleInstance> generateInstances(Long id) {
		if (id == null) {
			throw new BadRequestException("스케줄 ID가 없습니다.");
		}

		Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new NotFoundException("스케줄을 찾을 수 없습니다."));

		return generateInstances(schedule);
	}

	public List<ScheduleInstance> generateInstances(Schedule schedule) {
		if (schedule == null) {
			throw new BadRequestException("스케줄 정보가 없습니다.");
		}

		Long scheduleId = schedule.getId();
		if (scheduleId == null) {
			throw new BadRequestException("스케줄 ID가 없습니다.");
		}

		// 기존 인스턴스 삭제
		instanceRepository.deleteAllByScheduleId(scheduleId);

		// 발생 시점 계산
		List<LocalDateTime> times = scheduleService.generateOccurrences(schedule);

		// 인스턴스 생성
		for (LocalDateTime time : times) {
			ScheduleInstance instance = new ScheduleInstance();
			instance.setSchedule(schedule);
			instance.setOccurrenceTime(time);
			instanceRepository.save(instance);
		}

		return instanceRepository.findAllByScheduleId(scheduleId);
	}

	// 일정에서 발생 시간을 계산하는 내부 메소드 (scheduleService 대신 직접 계산)
	private List<LocalDateTime> generateOccurrences(Schedule schedule) {
		List<LocalDateTime> occurrences = new ArrayList<>();
		RecurrenceRule rule = schedule.getRecurrenceRule();
		if (rule == null)
			return occurrences;

		LocalDate current = schedule.getStartDate();
		LocalDate end = schedule.getEndDate() != null ? schedule.getEndDate() : current.plusMonths(3);
		int interval = rule.getInterval() != null ? rule.getInterval() : 1;

	    // untilDate가 end보다 짧으면 untilDate 우선
	    if (rule.getUntilDate() != null && rule.getUntilDate().isBefore(end)) {
	        end = rule.getUntilDate();
	    }

	    int count = 0;
	    int maxCount = rule.getRepeatCount() != null ? rule.getRepeatCount() : Integer.MAX_VALUE;
		
		switch (rule.getType()) {
		case DAILY -> {
			while (!current.isAfter(end) && count < maxCount) {
				occurrences.add(LocalDateTime.of(current, schedule.getScheduleTime().toLocalTime()));
				current = current.plusDays(interval);
				count++;
			}
		}
		case WEEKLY -> {
			List<DayOfWeek> days = rule.getDaysOfWeek() != null ? rule.getDaysOfWeek() : List.of();
			while (!current.isAfter(end) && count < maxCount) {
				for (DayOfWeek day : days) {
					LocalDate d = current.with(java.time.temporal.TemporalAdjusters.nextOrSame(day));
					if (!d.isAfter(end) && count < maxCount) {
						occurrences.add(LocalDateTime.of(d, schedule.getScheduleTime().toLocalTime()));
						count++;
					}
				}
				current = current.plusWeeks(interval);
			}
		}
		case MONTHLY -> {
		    // case 1: dayOfMonth 지정 (예: 매달 15일)
		    if (rule.getDayOfMonth() != null) {
		        while (!current.isAfter(end) && count < maxCount) {
		            int day = Math.min(rule.getDayOfMonth(), current.lengthOfMonth());
		            LocalDate d = current.withDayOfMonth(day);

		            // 시작일 이후만 생성
		            if (!d.isBefore(schedule.getStartDate()) && !d.isAfter(end)) {
		                occurrences.add(LocalDateTime.of(d, schedule.getScheduleTime().toLocalTime()));
		                count++;
		            }

		            current = current.plusMonths(interval);
		        }
		    }
		    // case 2: nthWeek + dayOfWeek(s) (예: 매달 첫째주 월요일, 수요일)
		    else if (rule.getNthWeek() != null) {
		        // 단일 dayOfWeek(옵션) + 리스트 daysOfWeek(옵션) 둘 다 고려
		        List<DayOfWeek> days = new ArrayList<>();
		        if (rule.getDaysOfWeek() != null && !rule.getDaysOfWeek().isEmpty()) {
		            days.addAll(rule.getDaysOfWeek());
		        }
		        if (rule.getDaysOfWeek() != null && !days.contains(rule.getDaysOfWeek())) {
		            days.addAll(rule.getDaysOfWeek());
		        }

		        // 아무 요일도 지정 안 됐다면 안전하게 종료
		        if (days.isEmpty()) {
		            throw new BadRequestException("MONTHLY(Nth) 반복은 dayOfWeek 혹은 daysOfWeek가 필요합니다.");
		        }

		        // 월 반복 루프
		        while (!current.isAfter(end) && count < maxCount) {
		            LocalDate firstOfMonth = current.withDayOfMonth(1);

		            // 지정된 여러 요일 각각에 대해 "그 달의 N번째 해당 요일" 생성
		            for (DayOfWeek day : days) {
		                if (count >= maxCount) break;

		                LocalDate target = firstOfMonth.with(
		                        TemporalAdjusters.dayOfWeekInMonth(rule.getNthWeek(), day));

		                // target이 해당 달에 존재하고(일부 조합은 존재하지 않을 수 있음), 범위 내일 때만 추가
		                if (target.getMonth().equals(firstOfMonth.getMonth())
		                        && !target.isBefore(schedule.getStartDate())
		                        && !target.isAfter(end)) {
		                    occurrences.add(LocalDateTime.of(target, schedule.getScheduleTime().toLocalTime()));
		                    count++;
		                }
		            }

		            current = current.plusMonths(interval);
		        }
		    }
		    else {
		        throw new BadRequestException("MONTHLY 반복은 dayOfMonth 또는 (nthWeek + dayOfWeek[s])가 필요합니다.");
		    }
		}

        default -> throw new IllegalArgumentException("지원하지 않는 반복 유형입니다: " + rule.getType());
    }

		return occurrences;
	}

	// 특정 일정의 모든 발생 인스턴스 조회
	@Transactional(readOnly = true)
	public List<ScheduleInstance> getInstances(Long scheduleId) {
		return instanceRepository.findAllByScheduleId(scheduleId);
	}

	// 특정 인스턴스 완료 처리 (추후 확장용)
	public void markCompleted(Long instanceId) {
		ScheduleInstance instance = instanceRepository.findById(instanceId)
				.orElseThrow(() -> new NotFoundException("해당 인스턴스가 존재하지 않습니다."));
		instance.setCompleted(true);
		instanceRepository.save(instance);
	}

	@Transactional
	public void deleteAllByScheduleId(Long scheduleId) {
		if (scheduleId == null)
			throw new IllegalArgumentException("scheduleId가 비어있습니다.");
		instanceRepository.deleteAllByScheduleId(scheduleId);
	}

	@Transactional
	public void regenerateInstances(Schedule schedule) {
	    Long scheduleId = schedule.getId();
	    List<ScheduleInstance> existing = instanceRepository.findAllByScheduleId(scheduleId);

	    // 완료되지 않은 인스턴스만 삭제
	    existing.stream()
	            .filter(i -> !i.getCompleted())
	            .forEach(instanceRepository::delete);

	    // 새로운 발생 시점 생성
	    List<LocalDateTime> times = generateOccurrences(schedule);
	    for (LocalDateTime time : times) {
	        boolean alreadyExists = existing.stream()
	                .anyMatch(i -> i.getOccurrenceTime().equals(time));
	        if (!alreadyExists) {
	            ScheduleInstance instance = new ScheduleInstance();
	            instance.setSchedule(schedule);
	            instance.setOccurrenceTime(time);
	            instanceRepository.save(instance);
	        }
	    }
	}
}
