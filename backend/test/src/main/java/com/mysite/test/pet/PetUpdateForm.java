package com.mysite.test.pet;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetUpdateForm {
    
    // 수정할 반려동물의 정보
    private String petname; 

    @PastOrPresent(message = "생년월일은 미래 날짜일 수 없습니다.")
    private LocalDate birthDate;

    private Boolean neuter; 

    @DecimalMin(value = "0.1", message = "몸무게는 0.1 kg 이상이어야 합니다.")
    private Double weight;
}