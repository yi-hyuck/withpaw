// ScheduleResponseDTO.java
package com.mysite.test.schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ScheduleResponseDTO {
    private Long id;
    private Long memberId;
    private String title;
    private Boolean recurring;

    // 단일 일정일 때만 채워짐
    private LocalDateTime scheduleTime;

    // 반복일정 정보 (반복일정일 때만 채워짐)
    private RecurrenceType recurrenceType;
    private Integer interval;
    private List<DayOfWeek> daysOfWeek;
    private Integer dayOfMonth;
    private Integer repeatCount;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate untilDate;
    private Integer remindBeforeMinutes;
    
    public static ScheduleResponseDTO fromEntity(Schedule s) {
        RecurrenceRule rule = s.getRecurrenceRule();

        return ScheduleResponseDTO.builder()
                .id(s.getId())
                .memberId(s.getMemberId())
                .title(s.getTitle())
                .recurring(s.getRecurring())
                .scheduleTime(s.getScheduleTime())
                .recurrenceType(rule != null ? rule.getType() : null)
                .interval(rule != null ? rule.getInterval() : null)
                .daysOfWeek(rule != null ? rule.getDaysOfWeek() : null)
                .dayOfMonth(rule != null ? rule.getDayOfMonth() : null)
                .repeatCount(rule != null ? rule.getRepeatCount() : null)
                .untilDate(rule != null ? rule.getUntilDate() : null)
                .startDate(s.getStartDate())
                .endDate(s.getEndDate())
                .remindBeforeMinutes(s.getRemindBeforeMinutes())
                .build();
    }
}
