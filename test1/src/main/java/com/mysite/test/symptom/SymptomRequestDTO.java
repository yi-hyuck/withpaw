package com.mysite.test.symptom;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SymptomRequestDTO {
//	서버에서 Principal을 통해 설정되므로 제약 조건 제거
//	@NotNull(message="회원id를 입력해야 합니다.")
//	@Positive(message = "회원 ID는 양수여야 합니다.")
	private Long memberId;
	
    @NotBlank(message="제목을 입력해야 합니다.")
    private String title;
    
    private LocalDateTime symptomDate;
	
    @NotBlank(message = "증상 설명을 입력해야 합니다.")
    private String description;

    public Symptom toEntity() {
        Symptom s = new Symptom();
        s.setMemberId(memberId);
        s.setSymptomDate(symptomDate);
        s.setTitle(title);
        s.setDescription(description);
        return s;
    }
    
}