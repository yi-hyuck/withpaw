package com.mysite.test.schedule;

import java.time.LocalDateTime;
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
}
