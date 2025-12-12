package com.mysite.test.schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.mysite.test.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ScheduleInstanceGenerator {

	private final ScheduleInstanceRepository instanceRepository;

    public void generateInstances(Schedule schedule, List<LocalDateTime> occurrences) { // <-- 시그니처 변경
        if (schedule == null) {
            throw new BadRequestException("스케줄 데이터가 비어 있습니다.");
        }

        // 기존 인스턴스 삭제 (덮어쓰기 방지) 
        instanceRepository.deleteAllByScheduleId(schedule.getId());

        if (occurrences == null || occurrences.isEmpty()) {
            return;
        }

        List<ScheduleInstance> instances = occurrences.stream()
                .map(time -> new ScheduleInstance(schedule, time))
                .toList();

        instanceRepository.saveAll(instances);
    }
    
    public void deleteInstancesByScheduleId(Long scheduleId) {
        instanceRepository.deleteAllByScheduleId(scheduleId);
    }
    
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
}
