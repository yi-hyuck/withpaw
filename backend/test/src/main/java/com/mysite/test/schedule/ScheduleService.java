package com.mysite.test.schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
	
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

	//프론트에서 날짜, 시간 따로 받음 -> 합치는 코드
	private LocalDateTime combineDateAndtime(LocalDate date, String timeStr) {
		if(date == null || timeStr == null || timeStr.isBlank()) {
			throw new BadRequestException("날짜와 시간 정보가 올바르지 않습니다.");
		}
		try {
			LocalTime time = LocalTime.parse(timeStr, TIME_FORMATTER);
			return LocalDateTime.of(date, time);
		} catch(Exception e) {
			throw new BadRequestException("시간 형식이 올바르지 않습니다.");
		}
	}
	
    // 일정 등록 (자동 인스턴스 생성 포함)
    @Transactional
    public ScheduleResponseDTO create(ScheduleRequestDTO dto) {
        boolean isRecurring = Boolean.TRUE.equals(dto.getRecurring());
        
        // 유효성 검증
        if (isRecurring) {
            if (dto.getInterval() == null || dto.getInterval() <= 0)
                throw new BadRequestException("반복 간격은 1 이상이어야 합니다.");
            if (dto.getStartDate() == null || dto.getEndDate() == null)
                throw new BadRequestException("반복 일정의 시작일은 반드시 지정해야 합니다.");
        } else {
            if (dto.getStartDate() == null || dto.getEndDate() == null)
                throw new BadRequestException("단일 일정은 시간을 지정해야 합니다.");
        }
        
        if(dto.getTime() == null || dto.getTime().isBlank()) {
        	throw new BadRequestException("일정 시간을 지정해야 합니다.");
        }

        try {
            // Schedule 생성
        	Schedule s = new Schedule();
    		s.setMemberId(dto.getMemberId());
    		s.setTitle(dto.getTitle());
    		s.setRecurring(dto.getRecurring());
    		s.setStartDate(dto.getStartDate());
    		s.setEndDate(dto.getEndDate());
    		s.setRemindBeforeMinutes(dto.getRemindBeforeMinutes());
    		
    		s.setScheduleTime(combineDateAndtime(dto.getStartDate(), dto.getTime()));

            if (Boolean.TRUE.equals(dto.getRecurring())) {
                RecurrenceRule rule = new RecurrenceRule();
                rule.setType(RecurrenceType.WEEKLY);
                
                DayOfWeek startDay = dto.getStartDate().getDayOfWeek();
                rule.setDaysOfWeek(List.of(startDay));
                
                rule.setInterval(dto.getInterval() != null && dto.getInterval() > 0 ? dto.getInterval() : 1);
                
                s.setRecurrenceRule(rule);              
            } else {
            	//단일일정
            	s.setStartDate(dto.getStartDate());
            	s.setEndDate(dto.getEndDate());
            	s.setRecurrenceRule(null);
            }
            
            // DB 저장
            Schedule savedSchedule = scheduleRepository.save(s);
            savedSchedule = scheduleRepository.findById(savedSchedule.getId())
                    .orElseThrow(() -> new InternalServerException("저장된 일정을 다시 조회할 수 없습니다."));
            
            // 자동 인스턴스 생성
            List<LocalDateTime> occurrences = generateOccurrences(savedSchedule);
    		instanceGenerator.generateInstances(savedSchedule, occurrences);

    		return ScheduleResponseDTO.fromEntity(savedSchedule);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("일정 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


	// 단일 조회
	public ScheduleResponseDTO getById(Long id) {
		Schedule s = scheduleRepository.findById(id).orElseThrow(() -> new NotFoundException("해당 일정이 존재하지 않습니다."));
		return ScheduleResponseDTO.fromEntity(s);
	}

	// 회원별 조회
	public List<ScheduleResponseDTO> getByMemberId(Integer memberId) {
		List<Schedule> list = scheduleRepository.findAllByMemberId(memberId);
//		if (list.isEmpty())
//			return list;
		return list.stream().map(ScheduleResponseDTO::fromEntity).toList();
	}

    // 일정 삭제
    public void delete(Long id) {
        if (!scheduleRepository.existsById(id))
            throw new NotFoundException("삭제할 일정이 존재하지 않습니다.");

        try {
            instanceGenerator.deleteInstancesByScheduleId(id);
            scheduleRepository.deleteById(id);
        } catch (Exception e) {
            throw new InternalServerException("일정 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

	// 반복 일정 실제 발생 시점 계산 
    public List<LocalDateTime> generateOccurrences(Schedule schedule){
        List<LocalDateTime> occurrences = new ArrayList<>();
        RecurrenceRule rule = schedule.getRecurrenceRule();
        
        // 단일 일정이나 규칙이 없으면 빈 목록 반환
        if(rule == null || Boolean.FALSE.equals(schedule.getRecurring())) { //
            if (schedule.getScheduleTime() != null && Boolean.FALSE.equals(schedule.getRecurring())) {
                 return List.of(schedule.getScheduleTime());
            }
    		return occurrences;
    	}
    	
    	if(schedule.getScheduleTime() == null) {
    		throw new BadRequestException("반복 일정의 기준 시간 정보 없음");
    	}
    	
    	LocalDate start = schedule.getStartDate();
    	LocalDate end = schedule.getEndDate();
    	LocalTime time = schedule.getScheduleTime().toLocalTime();
    	
    	int interval = rule.getInterval() != null && rule.getInterval() > 0 ? rule.getInterval() : 1;
    	
    	if(start == null || end == null) {
    		throw new BadRequestException("반복 일정의 시작일과 종료일 없음");
    	}
    	
        LocalDate current = start;
    	
        if (rule.getType() == RecurrenceType.WEEKLY) {
            while(!current.isAfter(end)) { //
                occurrences.add(LocalDateTime.of(current, time)); //
                current = current.plusWeeks(interval); //
            }
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
