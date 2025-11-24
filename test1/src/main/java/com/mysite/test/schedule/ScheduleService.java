package com.mysite.test.schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.test.exception.BadRequestException;
import com.mysite.test.exception.InternalServerException;
import com.mysite.test.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

	private final ScheduleRepository scheduleRepository;
	private final ScheduleInstanceGenerator instanceGenerator;

    // 일정 등록 (자동 인스턴스 생성 포함)
    @Transactional
    public ScheduleResponseDTO create(ScheduleRequestDTO dto) {
        boolean isRecurring = Boolean.TRUE.equals(dto.getRecurring());
        
        // 유효성 검증
        if (isRecurring) {
            if (dto.getRecurrenceType() == null || dto.getRecurrenceType().isBlank())
                throw new BadRequestException("반복 일정은 반복 유형을 지정해야 합니다.");
            if (dto.getInterval() == null || dto.getInterval() <= 0)
                throw new BadRequestException("반복 간격은 1 이상이어야 합니다.");
            if ("WEEKLY".equalsIgnoreCase(dto.getRecurrenceType()) &&
                (dto.getDaysOfWeek() == null || dto.getDaysOfWeek().isEmpty()))
                throw new BadRequestException("주간 반복 일정은 요일을 지정해야 합니다.");
            if (dto.getStartDate() == null)
                throw new BadRequestException("반복 일정의 시작일은 반드시 지정해야 합니다.");
            if (dto.getScheduleTime() == null)
                throw new BadRequestException("반복 일정은 기준 시간이 필요합니다. 시간을 입력하세요.");
        } else {
            if (dto.getScheduleTime() == null)
                throw new BadRequestException("단일 일정은 시간을 지정해야 합니다.");
        }

        try {
            // Schedule 생성
            Schedule s = new Schedule();
            s.setMemberId(dto.getMemberId());
            s.setTitle(dto.getTitle());
            s.setRecurring(isRecurring);
            s.setScheduleTime(dto.getScheduleTime());
            s.setRemindBeforeMinutes(dto.getRemindBeforeMinutes());

            if (isRecurring) {
                RecurrenceRule rule = new RecurrenceRule();
                rule.setType(RecurrenceType.valueOf(dto.getRecurrenceType().trim().toUpperCase()));
                rule.setInterval(dto.getInterval());

                if (rule.getType() == RecurrenceType.WEEKLY) {
                    rule.setDaysOfWeek(dto.getDaysOfWeek());
                }
                if (rule.getType() == RecurrenceType.MONTHLY && dto.getDayOfMonth() != null) {
                    rule.setDayOfMonth(dto.getDayOfMonth());
                }

                rule.setRepeatCount(dto.getRepeatCount());
                rule.setUntilDate(dto.getUntilDate());

                s.setRecurrenceRule(rule);
                s.setStartDate(dto.getStartDate());
                s.setEndDate(dto.getEndDate());
                
            }
            // DB 저장
            Schedule saved = scheduleRepository.save(s);
            saved = scheduleRepository.findById(saved.getId())
                    .orElseThrow(() -> new InternalServerException("저장된 일정을 다시 조회할 수 없습니다."));
            
            // 자동 인스턴스 생성
            instanceGenerator.generateInstances(saved);

            return ScheduleResponseDTO.fromEntity(saved);

        } catch (IllegalArgumentException e) {
            throw new BadRequestException("반복 유형 값이 올바르지 않습니다. DAILY/WEEKLY/MONTHLY 중 하나여야 합니다.");
        } catch (Exception e) {
            throw new InternalServerException("일정 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 일정 수정
    @Transactional
    public ScheduleResponseDTO update(Long id, ScheduleUpdateDTO dto) {
        Schedule existing = scheduleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 일정이 존재하지 않습니다."));

        boolean isRecurring = Boolean.TRUE.equals(dto.getRecurring());

        try {
            if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
                existing.setTitle(dto.getTitle());
            }

            if (dto.getRecurring() != null) {
                existing.setRecurring(dto.getRecurring());
            }

            if (isRecurring) {
                // recurrenceRule이 null이면 새로 생성
                if (existing.getRecurrenceRule() == null) {
                    existing.setRecurrenceRule(new RecurrenceRule());
                }

                RecurrenceRule rule = existing.getRecurrenceRule();

                if (dto.getRecurrenceType() != null && !dto.getRecurrenceType().isBlank()) {
                    rule.setType(RecurrenceType.valueOf(dto.getRecurrenceType().toUpperCase()));
                }

                if (dto.getInterval() != null) {
                    if (dto.getInterval() <= 0)
                        throw new BadRequestException("반복 간격은 1 이상이어야 합니다.");
                    rule.setInterval(dto.getInterval());
                }

                if ("WEEKLY".equalsIgnoreCase(dto.getRecurrenceType())) {
                    if (dto.getDaysOfWeek() == null || dto.getDaysOfWeek().isEmpty()) {
                        throw new BadRequestException("주간 반복 일정은 요일을 지정해야 합니다.");
                    }
                    rule.setDaysOfWeek(dto.getDaysOfWeek());
                }

                if ("MONTHLY".equalsIgnoreCase(dto.getRecurrenceType())) {
                    if (dto.getDayOfMonth() == null || dto.getDayOfMonth() < 1 || dto.getDayOfMonth() > 31) {
                        throw new BadRequestException("매월 반복 일정은 1~31일 사이의 날짜를 지정해야 합니다.");
                    }
                    rule.setDayOfMonth(dto.getDayOfMonth());
                }

                if (dto.getRepeatCount() != null) {
                    rule.setRepeatCount(dto.getRepeatCount());
                }

                if (dto.getStartDate() != null) {
                    existing.setStartDate(dto.getStartDate());
                }

                if (dto.getEndDate() != null) {
                    if (existing.getStartDate() != null && dto.getEndDate().isBefore(existing.getStartDate()))
                        throw new BadRequestException("종료일은 시작일보다 빠를 수 없습니다.");
                    existing.setEndDate(dto.getEndDate());
                }

                // untilDate 추가
                if (dto.getUntilDate() != null) {
                    rule.setUntilDate(dto.getUntilDate());
                }

            } else {
                // 반복 아님 → 단일 일정으로 변경 시 recurrenceRule 초기화 가능
                existing.setRecurrenceRule(null);
            }

            Schedule updated = scheduleRepository.save(existing);

            return ScheduleResponseDTO.fromEntity(updated);

        } catch (BadRequestException | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("일정 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

	// 단일 조회
	public ScheduleResponseDTO getById(Long id) {
		Schedule s = scheduleRepository.findById(id).orElseThrow(() -> new NotFoundException("해당 일정이 존재하지 않습니다."));
		return ScheduleResponseDTO.fromEntity(s);
	}

	// 회원별 조회
	public List<ScheduleResponseDTO> getByMemberId(Long memberId) {
		List<Schedule> list = scheduleRepository.findAllByMemberId(memberId);
		if (list.isEmpty())
			throw new NotFoundException("등록된 일정이 없습니다.");
		return list.stream().map(ScheduleResponseDTO::fromEntity).toList();
	}

    // 일정 삭제
    public void delete(Long id) {
        if (!scheduleRepository.existsById(id))
            throw new NotFoundException("삭제할 일정이 존재하지 않습니다.");

        try {
            instanceGenerator.deleteInstancesByScheduleId(id); // 연관 인스턴스도 함께 삭제
            scheduleRepository.deleteById(id);
        } catch (Exception e) {
            throw new InternalServerException("일정 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

	// 반복 일정 실제 발생 시점 계산 
	public List<LocalDateTime> generateOccurrences(Schedule schedule) {
	    if (schedule == null) {
	        throw new BadRequestException("일정 데이터가 없습니다.");
	    }

	    // 단일 일정인 경우
	    if (Boolean.FALSE.equals(schedule.getRecurring()) || schedule.getRecurrenceRule() == null) {
	        if (schedule.getScheduleTime() == null) {
	            throw new BadRequestException("단일 일정의 시간 정보가 없습니다.");
	        }
	        return List.of(schedule.getScheduleTime());
	    }

	    // 반복 일정인 경우
	    RecurrenceRule rule = schedule.getRecurrenceRule();
	    if (rule.getType() == null) {
	        throw new BadRequestException("반복 유형이 지정되지 않았습니다.");
	    }
	    if (schedule.getScheduleTime() == null) {
	        throw new BadRequestException("반복 일정의 기준 시간이 없습니다.");
	    }

	    List<LocalDateTime> occurrences = new ArrayList<>();

	    LocalDate current = schedule.getStartDate();
	    LocalDate end = schedule.getEndDate() != null ? schedule.getEndDate() : current.plusMonths(3);
	    int interval = rule.getInterval() != null ? rule.getInterval() : 1;

	    switch (rule.getType()) {
	        case DAILY -> {
	            while (!current.isAfter(end)) {
	                occurrences.add(LocalDateTime.of(current, schedule.getScheduleTime().toLocalTime()));
	                current = current.plusDays(interval);
	            }
	        }
	        case WEEKLY -> {
	            List<DayOfWeek> days = rule.getDaysOfWeek() != null ? rule.getDaysOfWeek() : List.of();
	            
	            while (!current.isAfter(end)) {
	                for (DayOfWeek day : days) {
	                    LocalDate d = current.with(java.time.temporal.TemporalAdjusters.nextOrSame(day));
	                    if (!d.isAfter(end))
	                        occurrences.add(LocalDateTime.of(d, schedule.getScheduleTime().toLocalTime()));
	                }
	                current = current.plusWeeks(interval);
	            }
	        }
	        case MONTHLY -> {
	            int day = rule.getDayOfMonth() != null
	                    ? rule.getDayOfMonth()
	                    : (schedule.getStartDate() != null ? schedule.getStartDate().getDayOfMonth() : 1);
	            while (!current.isAfter(end)) {
	                LocalDate d = current.withDayOfMonth(Math.min(day, current.lengthOfMonth()));
	                occurrences.add(LocalDateTime.of(d, schedule.getScheduleTime().toLocalTime()));
	                current = current.plusMonths(interval);
	            }
	        }
	        default -> throw new BadRequestException("지원하지 않는 반복 유형입니다.");
	    }

	    return occurrences;
	}
	
	// 반복 일정 발생일 계산
	public List<LocalDateTime> getOccurrences(Long id) {
	    // 일정 존재 여부 확인
	    Schedule schedule = scheduleRepository.findById(id)
	            .orElseThrow(() -> new NotFoundException("해당 일정이 존재하지 않습니다."));

	    // 반복 여부 및 규칙 유효성 검증
	    if (!Boolean.TRUE.equals(schedule.getRecurring()) || schedule.getRecurrenceRule() == null) {
	        throw new BadRequestException("이 일정은 반복 일정이 아닙니다.");
	    }

	    try {
	        RecurrenceRule rule = schedule.getRecurrenceRule();
	        LocalDate start = schedule.getStartDate();
	        LocalDate end = schedule.getEndDate();

	        if (start == null) {
	            throw new BadRequestException("반복 일정의 시작일(startDate)이 지정되어야 합니다.");
	        }

	        // 반복 일정 계산 로직
	        List<LocalDateTime> occurrences = new ArrayList<>();
	        LocalDate currentDate = start;
	        int count = 0;

	        while ((end == null || !currentDate.isAfter(end))
	                && (rule.getRepeatCount() == null || count < rule.getRepeatCount())) {

	            switch (rule.getType()) {
	                case DAILY -> {
	                    occurrences.add(currentDate.atStartOfDay());
	                    currentDate = currentDate.plusDays(rule.getInterval());
	                }
	                case WEEKLY -> {
	                    if (rule.getDaysOfWeek() != null && !rule.getDaysOfWeek().isEmpty()) {
	                        for (DayOfWeek day : rule.getDaysOfWeek()) {
	                            LocalDate next = currentDate.with(day);
	                            if ((end == null || !next.isAfter(end)) && !next.isBefore(start)) {
	                                occurrences.add(next.atStartOfDay());
	                            }
	                        }
	                    }
	                    currentDate = currentDate.plusWeeks(rule.getInterval());
	                }
	                case MONTHLY -> {
	                    int dayOfMonth = (rule.getDayOfMonth() != null) ? rule.getDayOfMonth() : 1;
	                    LocalDate next = currentDate.withDayOfMonth(
	                            Math.min(dayOfMonth, currentDate.lengthOfMonth())
	                    );
	                    occurrences.add(next.atStartOfDay());
	                    currentDate = currentDate.plusMonths(rule.getInterval());
	                }
	                default -> throw new BadRequestException("지원하지 않는 반복 유형입니다.");
	            }

	            count++;
	        }

	        if (occurrences.isEmpty()) {
	            throw new NotFoundException("해당 규칙에 따라 생성된 반복 일정이 없습니다.");
	        }

	        return occurrences;

	    } catch (BadRequestException | NotFoundException e) {
	        throw e;
	    } catch (Exception e) {
	        throw new InternalServerException("반복 일정 계산 중 오류가 발생했습니다: " + e.getMessage());
	    }
	}	


}
