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
	@NotNull(message="동물id를 입력해야 합니다.")
	@Positive(message = "반려동물 ID는 양수여야 합니다.")
    private Long petId;
	@NotNull(message="날짜를 지정해야 합니다.")
    private LocalDateTime symptomDate;
	@NotBlank(message = "증상 설명을 입력해야 합니다.")
    private String description;
	@NotNull(message = "선택된 증상 목록은 비워둘 수 없습니다.")
	@jakarta.validation.constraints.Size(min = 1, message = "선택된 증상 목록은 1개 이상이어야 합니다.")
	private List<Long> selectedSymptomIds; // 선택된 증상 ID들
    private List<Long> suspectedDiseaseIds; //관련질병id
    
    public Symptom toEntity() {
        Symptom s = new Symptom();
        s.setMemberId(memberId);
        s.setPetId(petId);
        s.setSymptomDate(symptomDate);
        s.setDescription(description);
        
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            String selectedJson = mapper.writeValueAsString(selectedSymptomIds);
            s.setSelectedSymptomIds(selectedJson);
        } catch (Exception e) {
            s.setSelectedSymptomIds("[]");
        }
        
        try {
            String suspectedJson = mapper.writeValueAsString(suspectedDiseaseIds);
            s.setSuspectedDiseaseIds(suspectedJson);
        } catch (Exception e) {
            s.setSuspectedDiseaseIds("[]");
        }

        return s;
    }
    
}