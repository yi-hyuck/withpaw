package com.mysite.test.schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleRequestDTO {

    @NotNull(message = "회원 ID는 필수입니다.")
    private Long memberId;

    @NotBlank(message = "일정 제목은 비워둘 수 없습니다.")
    private String title;

    // 단일 일정일 때만 필수
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduleTime;

    // 반복 여부 (true/false)
    @NotNull(message = "반복 여부를 선택해야 합니다.")
    private Boolean recurring;

    // 반복 유형: DAILY, WEEKLY, MONTHLY
    private String recurrenceType;

    // 반복 간격(>=1)
    private Integer interval;

    // 요일 목록을 CSV 문자열로 받음: "MONDAY,FRIDAY"
    private List<DayOfWeek> daysOfWeek;

    // 매월 n일
    private Integer dayOfMonth;

    // 반복 횟수(선택)
    private Integer repeatCount;

    // 반복 시작/종료일
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate untilDate;
    @Min(1)
    private Integer remindBeforeMinutes;
}
