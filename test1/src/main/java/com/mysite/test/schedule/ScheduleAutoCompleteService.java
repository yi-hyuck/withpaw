package com.mysite.test.schedule;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleAutoCompleteService {

    private final ScheduleInstanceRepository instanceRepository;

    // 5분마다 자동 실행
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void markPastSchedulesAsCompleted() {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduleInstance> past = instanceRepository.findByCompletedFalseAndOccurrenceTimeBefore(now);

        for (ScheduleInstance instance : past) {
            instance.setCompleted(true);
        }
        instanceRepository.saveAll(past);
    }
}
