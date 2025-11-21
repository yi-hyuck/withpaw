package com.spring.dto;


import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.domain.Symptom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SymptomRequestDTO {
	@NotNull(message="회원id를 입력해야 합니다.")
	@Positive(message = "회원 ID는 양수여야 합니다.")
	private Long memberId;

	@NotNull(message="날짜를 지정해야 합니다.")
    private LocalDateTime symptomDate;
	@NotBlank(message = "증상 설명을 입력해야 합니다.")
    private String description;

    
    public Symptom toEntity() {
        Symptom s = new Symptom();
        s.setMemberId(memberId);
//        s.setPetId(petId);
        s.setSymptomDate(symptomDate);
        s.setDescription(description);

        return s;
    }
    
}