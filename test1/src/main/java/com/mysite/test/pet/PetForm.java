package com.mysite.test.pet;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetForm {
	@NotBlank(message = "이름은 필수 항목입니다.")
    private String name; 

    @NotBlank(message = "종류는 필수 항목입니다.")
    private String species; 

    @NotBlank(message = "성별은 필수 항목입니다.")
    private String gender;

    @PastOrPresent(message = "생년월일은 미래 날짜일 수 없습니다.")
    @NotNull(message = "생년월일은 필수 항목입니다.")
    private LocalDate birthDate;

    @NotNull(message = "중성화 여부는 필수 항목입니다.")
    private Boolean neuter; 

    @NotNull(message = "몸무게는 필수 항목입니다.")
    @DecimalMin(value = "0.1", message = "몸무게는 0.1 kg 이상이어야 합니다.")
    private Double weight;
	
}
