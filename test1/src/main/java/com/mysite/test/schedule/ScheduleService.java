package com.mysite.test.schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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


            // Schedule 생성
            Schedule s = new Schedule();
            s.setMemberId(dto.getMemberId());
            s.setTitle(dto.getTitle());
            s.setRecurring(isRecurring);
            s.setScheduleTime(dto.getScheduleTime());
            s.setRemindBeforeMinutes(dto.getRemindBeforeMinutes());

            if (isRecurring) {
                RecurrenceRule rule = new RecurrenceRule();
                try {
                    rule.setType(
                            RecurrenceType.valueOf(dto.getRecurrenceType().trim().toUpperCase())
                    );
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("반복 유형 값이 올바르지 않습니다. DAILY/WEEKLY/MONTHLY 중 하나여야 합니다.");
                }
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
            // 자동 인스턴스 생성
            instanceGenerator.generateInstances(saved);

            return ScheduleResponseDTO.fromEntity(saved); 
    }

    // 일정 수정
    @Transactional
    public ScheduleResponseDTO update(Long id, ScheduleUpdateDTO dto) {
        Schedule existing = scheduleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 일정이 존재하지 않습니다."));

        Boolean newRecurring = dto.getRecurring() != null
                ? dto.getRecurring()
                : existing.getRecurring();
        boolean isRecurring = Boolean.TRUE.equals(newRecurring);
        existing.setRecurring(newRecurring);

            if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
                existing.setTitle(dto.getTitle());
            }

            if (dto.getRecurring() != null) {
                existing.setRecurring(dto.getRecurring());
            }
            
            if (dto.getScheduleTime() != null) {
                existing.setScheduleTime(dto.getScheduleTime());
            }
            
            if (dto.getRemindBeforeMinutes() != null) {
                if (dto.getRemindBeforeMinutes() < 1) {
                    throw new BadRequestException("리마인드 분은 1 이상이어야 합니다.");
                }
                existing.setRemindBeforeMinutes(dto.getRemindBeforeMinutes());
            }

            if (isRecurring) {
                // recurrenceRule이 null이면 새로 생성
                if (existing.getRecurrenceRule() == null) {
                    existing.setRecurrenceRule(new RecurrenceRule());
                }

                RecurrenceRule rule = existing.getRecurrenceRule();

                if (dto.getRecurrenceType() != null && !dto.getRecurrenceType().isBlank()) {
                    try {
                        rule.setType(RecurrenceType.valueOf(dto.getRecurrenceType().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new BadRequestException("반복 유형 값이 올바르지 않습니다. DAILY/WEEKLY/MONTHLY 중 하나여야 합니다.");
                    }
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

            instanceGenerator.deleteInstancesByScheduleId(id); // 연관 인스턴스도 함께 삭제
            scheduleRepository.deleteById(id);

    }

 // 공통 반복 발생일 계산 로직
    private List<LocalDateTime> calculateOccurrences(Schedule schedule) {
        if (schedule == null) {
            throw new BadRequestException("일정 데이터가 없습니다.");
        }

        // 반복 아님 -> 단일 일정
        if (!Boolean.TRUE.equals(schedule.getRecurring()) || schedule.getRecurrenceRule() == null) {
            if (schedule.getScheduleTime() == null) {
                throw new BadRequestException("단일 일정의 시간 정보가 없습니다.");
            }
            return List.of(schedule.getScheduleTime());
        }

        RecurrenceRule rule = schedule.getRecurrenceRule();

        if (rule.getType() == null) {
            throw new BadRequestException("반복 유형이 지정되지 않았습니다.");
        }
        if (schedule.getScheduleTime() == null) {
            throw new BadRequestException("반복 일정의 기준 시간이 없습니다.");
        }

        LocalDate start = schedule.getStartDate();
        if (start == null) {
            throw new BadRequestException("반복 일정의 시작일(startDate)이 지정되어야 합니다.");
        }

        // interval 기본값 1 + 검증
        int interval = (rule.getInterval() != null) ? rule.getInterval() : 1;
        if (interval <= 0) {
            throw new BadRequestException("반복 간격은 1 이상이어야 합니다.");
        }

        // endDate / untilDate / 기본 3개월 중에서 최종 종료일 결정
        LocalDate end;
        if (schedule.getEndDate() != null) {
            end = schedule.getEndDate();
        } else if (rule.getUntilDate() != null) {
            end = rule.getUntilDate();
        } else {
            end = start.plusMonths(3);
        }
        if (rule.getUntilDate() != null && rule.getUntilDate().isBefore(end)) {
            end = rule.getUntilDate();
        }

        List<LocalDateTime> occurrences = new ArrayList<>();
        LocalDate current = start;
        int count = 0;
        var time = schedule.getScheduleTime().toLocalTime();

        while (!current.isAfter(end)
                && (rule.getRepeatCount() == null || count < rule.getRepeatCount())) {

            switch (rule.getType()) {
                case DAILY -> {
                    // 매 interval일마다 한 번
                    occurrences.add(LocalDateTime.of(current, time));
                    current = current.plusDays(interval);
                }
                case WEEKLY -> {
                    // 주 단위 + 여러 요일
                    List<DayOfWeek> days = (rule.getDaysOfWeek() != null)
                            ? rule.getDaysOfWeek()
                            : List.of();

                    if (days.isEmpty()) {
                        throw new BadRequestException("주간 반복 일정은 요일을 지정해야 합니다.");
                    }

                    for (DayOfWeek day : days) {
                        LocalDate next = current.with(
                                java.time.temporal.TemporalAdjusters.nextOrSame(day)
                        );
                        if (!next.isBefore(start) && !next.isAfter(end)) {
                            occurrences.add(LocalDateTime.of(next, time));
                        }
                    }
                    current = current.plusWeeks(interval);
                }
                case MONTHLY -> {
                    int dayOfMonth = (rule.getDayOfMonth() != null)
                            ? rule.getDayOfMonth()
                            : start.getDayOfMonth();

                    LocalDate next = current.withDayOfMonth(
                            Math.min(dayOfMonth, current.lengthOfMonth())
                    );
                    if (!next.isBefore(start) && !next.isAfter(end)) {
                        occurrences.add(LocalDateTime.of(next, time));
                    }
                    current = current.plusMonths(interval);
                }
                default -> throw new BadRequestException("지원하지 않는 반복 유형입니다.");
            }
            count++;
        }

        return occurrences;
    }


    public List<LocalDateTime> generateOccurrences(Schedule schedule) {
        return calculateOccurrences(schedule);
    }
    
    public List<LocalDateTime> getOccurrences(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 일정이 존재하지 않습니다."));

        if (!Boolean.TRUE.equals(schedule.getRecurring()) || schedule.getRecurrenceRule() == null) {
            throw new BadRequestException("이 일정은 반복 일정이 아닙니다.");
        }

        List<LocalDateTime> occurrences = calculateOccurrences(schedule);

        if (occurrences.isEmpty()) {
            throw new NotFoundException("해당 규칙에 따라 생성된 반복 일정이 없습니다.");
        }

        return occurrences;
    }
    
    
}
