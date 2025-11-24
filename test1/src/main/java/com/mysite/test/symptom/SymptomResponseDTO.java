package com.mysite.test.symptom;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SymptomResponseDTO {
; 
	
	private Long id; // 증상 기록 ID
	private Long memberId; // 회원 ID
	private LocalDateTime symptomDate; // 기록된 날짜
	private String title; //제목
	private String description; // 기록 내용
	private LocalDateTime createdAt; // 생성 시각

	public static SymptomResponseDTO fromEntity(Symptom symptom) {
        SymptomResponseDTO dto = new SymptomResponseDTO();
        dto.setId(symptom.getId());
        dto.setMemberId(symptom.getMemberId());
        dto.setSymptomDate(symptom.getSymptomDate());
        dto.setTitle(symptom.getTitle());
        dto.setDescription(symptom.getDescription());
        dto.setCreatedAt(symptom.getCreatedAt());

        return dto;
    }
	

}

