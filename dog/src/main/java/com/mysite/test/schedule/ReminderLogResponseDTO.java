package com.mysite.test.schedule;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReminderLogResponseDTO {

    private Long id;
    private LocalDateTime reminderTime;
    private String message;
    private Boolean success;
    private Long scheduleInstanceId;

    public static ReminderLogResponseDTO fromEntity(ReminderLog log) {
        return ReminderLogResponseDTO.builder()
                .id(log.getId())
                .reminderTime(log.getReminderTime())
                .message(log.getMessage())
                .success(log.getSuccess())
                .scheduleInstanceId(
                    log.getScheduleInstance() != null
                        ? log.getScheduleInstance().getId()
                        : null
                )
                .build();
    }
}

