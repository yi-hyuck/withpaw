package com.mysite.test.pet;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class PetForm {
	private Integer petId;
	
	@NotBlank(message = "이름은 필수 항목입니다.")
    private String petname; 

    @NotBlank(message = "종류는 필수 항목입니다.")
    private String breed; 

    @NotBlank(message = "성별은 필수 항목입니다.")
    private String gender;

//    @PastOrPresent(message = "생년월일은 미래 날짜일 수 없습니다.")
    @NotNull(message = "생년월일은 필수 항목입니다.")
    private LocalDate birthdate;

    @NotNull(message = "중성화 여부는 필수 항목입니다.")
    private Boolean neuter;

    @NotNull(message = "몸무게는 필수 항목입니다.")
    @DecimalMin(value = "0.1", message = "몸무게는 0.1 kg 이상이어야 합니다.")
    private Double weight;
	
}
