package com.spring.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleUpdateDTO {

	private String title; // 일정 제목
	private LocalDateTime scheduleTime; // 단일 일정일 경우
	private Boolean recurring; // 반복 여부
	private String recurrenceType; // DAILY, WEEKLY, MONTHLY
	private Integer interval; // 반복 간격
	private List<DayOfWeek> daysOfWeek; // 주간 반복 요일 목록
	private Integer dayOfMonth; // 매월 n일
	private Integer repeatCount; // 반복 횟수
	private LocalDate startDate; // 시작일
	private LocalDate endDate; // 종료일
	private LocalDate untilDate;
	private Integer remindBeforeMinutes;
}
