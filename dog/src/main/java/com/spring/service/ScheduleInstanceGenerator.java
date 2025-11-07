package com.spring.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.spring.domain.RecurrenceRule;
import com.spring.domain.Schedule;
import com.spring.domain.ScheduleInstance;
import com.spring.exception.BadRequestException;
import com.spring.repository.ScheduleInstanceRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ScheduleInstanceGenerator {

    private final ScheduleInstanceRepository instanceRepository;

    public void generateInstances(Schedule schedule) {
        if (schedule == null) {
            throw new BadRequestException("스케줄 데이터가 비어 있습니다.");
        }

        // 기존 인스턴스 삭제 (덮어쓰기 방지)
        instanceRepository.deleteAllByScheduleId(schedule.getId());

        // 반복 여부에 따라 분기
        if (Boolean.FALSE.equals(schedule.getRecurring()) || schedule.getRecurrenceRule() == null) {
            if (schedule.getScheduleTime() == null)
                throw new BadRequestException("단일 일정의 시간 정보가 없습니다.");

            instanceRepository.save(new ScheduleInstance(schedule, schedule.getScheduleTime()));
            return;
        }

        RecurrenceRule rule = schedule.getRecurrenceRule();
        LocalDate current = schedule.getStartDate();
        LocalDate end = schedule.getEndDate() != null ? schedule.getEndDate() : current.plusMonths(3);
        int interval = rule.getInterval() != null ? rule.getInterval() : 1;

        List<LocalDateTime> occurrences = new ArrayList<>();

        switch (rule.getType()) {
            case DAILY -> {
                while (!current.isAfter(end)) {
                    occurrences.add(LocalDateTime.of(current, schedule.getScheduleTime().toLocalTime()));
                    current = current.plusDays(interval);
                }
            }
            case WEEKLY -> {
                List<java.time.DayOfWeek> days = rule.getDaysOfWeek();
                while (!current.isAfter(end)) {
                    for (java.time.DayOfWeek d : days) {
                        LocalDate target = current.with(TemporalAdjusters.nextOrSame(d));
                        if (!target.isAfter(end))
                            occurrences.add(LocalDateTime.of(target, schedule.getScheduleTime().toLocalTime()));
                    }
                    current = current.plusWeeks(interval);
                }
            }
            case MONTHLY -> {
                int day = rule.getDayOfMonth() != null
                        ? rule.getDayOfMonth()
                        : schedule.getStartDate().getDayOfMonth();
                while (!current.isAfter(end)) {
                    LocalDate d = current.withDayOfMonth(Math.min(day, current.lengthOfMonth()));
                    occurrences.add(LocalDateTime.of(d, schedule.getScheduleTime().toLocalTime()));
                    current = current.plusMonths(interval);
                }
            }
            default -> throw new BadRequestException("지원하지 않는 반복 유형입니다.");
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
