package com.spring.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.domain.Disease;
import com.spring.domain.Symptom;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SymptomResponseDTO {

	private static final ObjectMapper mapper = new ObjectMapper(); 
	
	private Long id; // 증상 기록 ID
	private Long memberId; // 회원 ID
	private Long petId; // 반려동물 ID
	private LocalDateTime symptomDate; // 기록된 날짜
	private String description; // 기록 내용
	private String selectedSymptomIds; // 선택된 증상 ID 목록

	private List<DiseaseSummaryDTO> suspectedDiseases; // 의심 질병 리스트
	private LocalDateTime createdAt; // 생성 시각

	public static SymptomResponseDTO fromEntity(Symptom symptom) {
        SymptomResponseDTO dto = new SymptomResponseDTO();
        dto.setId(symptom.getId());
        dto.setMemberId(symptom.getMemberId());
        dto.setPetId(symptom.getPetId());
        dto.setSymptomDate(symptom.getSymptomDate());
        dto.setDescription(symptom.getDescription());
        dto.setSelectedSymptomIds(symptom.getSelectedSymptomIds());
        dto.setCreatedAt(symptom.getCreatedAt());

        
        
        
        
        // 질병 요약 리스트 (질병 정보가 연관되어 있을 경우)
        if (symptom.getSuspectedDiseaseIds() != null) {
            try {
                List<Long> diseaseIds = SymptomResponseDTO.mapper.readValue(
                    symptom.getSuspectedDiseaseIds(),
                    new TypeReference<List<Long>>() {}
                );
                dto.setSuspectedDiseases(
                    diseaseIds.stream()
                              .map(id -> {
                                  DiseaseSummaryDTO d = new DiseaseSummaryDTO();
                                  d.setId(id);
                                  return d;
                              })
                              .collect(Collectors.toList())
                );
            } catch (Exception e) {
                dto.setSuspectedDiseases(List.of());
            }
        }
        return dto;
    }

    @Getter
    @Setter
    public static class DiseaseSummaryDTO {
        private Long id;
        private String name;
        private String description;

        public static DiseaseSummaryDTO fromEntity(Disease disease) {
            DiseaseSummaryDTO dto = new DiseaseSummaryDTO();
            dto.setId(disease.getId());
            dto.setName(disease.getName());
            dto.setDescription(disease.getDescription());
            return dto;
        }
    }
}

