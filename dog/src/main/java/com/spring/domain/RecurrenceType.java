package com.spring.domain;

public enum RecurrenceType {
    DAILY,        // 매일
    WEEKLY,       // 주별 (요일 지정)
    MONTHLY,      // 매월
    CUSTOM        // 사용자 지정 (n일/n주 간격)
}