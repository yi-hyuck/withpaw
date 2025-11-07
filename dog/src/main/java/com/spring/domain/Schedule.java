package com.spring.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "schedule")
public class Schedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 일정 ID
	private Long memberId; // 회원 ID
	// private Long petId; // 반려동물 ID
	private String title; // 일정 제목
	// private String content; // 일정 내용
	private LocalDateTime scheduleTime;// 일정 시간
	private Boolean recurring; // 반복 여부
	//private String recurrenceType; // 반복 유형 (daily, weekly, monthly, custom)->일마다 (월,목,토...)주마다(1주마다,2주마다...) 달마다, n일마다(3,10,20...)								
	// private String recurrenceDetail; // 반복 정보->커스텀일경우만
	private LocalDate startDate; // 시작일
	private LocalDate endDate; // 종료일
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "recurrence_rule_id")
	private RecurrenceRule recurrenceRule; // 반복 규칙 연결
	@Column(name = "remind_before_minutes")
	private Integer remindBeforeMinutes; // ex: 30 → 30분 전 알림
}
