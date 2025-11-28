package com.mysite.test.schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "recurrence_rule")
public class RecurrenceRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private RecurrenceType type; // DAILY, WEEKLY, MONTHLY

    // 반복 간격(일/주/월)
    @Column(nullable = false, name="interval_value")
    private Integer interval = 1; // 예: 2 → 2일마다

    private Integer nthWeek;
    
    // WEEKLY일 때만 사용 (월~일)
    @ElementCollection
    @CollectionTable(
        name = "recurrence_rule_dow",
        joinColumns = @JoinColumn(name = "recurrence_rule_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", length = 9)
    private List<DayOfWeek> daysOfWeek = new ArrayList<>();

    // MONTHLY일 때 사용 (1~31)
    private Integer dayOfMonth;

    // 반복 횟수 (null이면 제한 없음)
    private Integer repeatCount;

    // 반복 종료일 (null이면 무제한)
    private LocalDate untilDate;

}
