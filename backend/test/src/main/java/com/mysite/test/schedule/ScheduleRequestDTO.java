package com.mysite.test.schedule;

import java.time.LocalDate;

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
//    @NotNull
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotBlank(message = "일정 시간은 필수입니다.")
    private String time;

    // 반복 여부 (true/false) repeats
    @NotNull(message = "반복 여부를 선택해야 합니다.")
    private Boolean recurring;

    // repeatWeek
    private Integer interval;

    // 반복 시작/종료일
    @NotNull(message = "시작 날짜는 필수입니다.")
    private LocalDate startDate;
    @NotNull(message = "종료 날짜는 필수입니다.")
    private LocalDate endDate;
//    
//    private LocalDate untilDate;  //endDate랑 뭐가 다른 것인가?
    @Min(1)
    private Integer remindBeforeMinutes;
}
