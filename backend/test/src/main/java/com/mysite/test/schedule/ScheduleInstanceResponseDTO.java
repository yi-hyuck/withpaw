package com.mysite.test.schedule;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ScheduleInstanceResponseDTO {

	private Long id;
	private LocalDateTime occurrenceTime;
	private Boolean completed;
	private String scheduleTitle;

	public static ScheduleInstanceResponseDTO fromEntity(ScheduleInstance instance) {
		ScheduleInstanceResponseDTO dto = new ScheduleInstanceResponseDTO();
		dto.id = instance.getId();
		dto.occurrenceTime = instance.getOccurrenceTime();
		dto.completed = instance.getCompleted();
		if (instance.getSchedule() != null) {
			dto.scheduleTitle = instance.getSchedule().getTitle();
		}
		return dto;
	}
}