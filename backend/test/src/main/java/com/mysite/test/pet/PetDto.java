package com.mysite.test.pet;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetDto {
    private Integer petId;
    private String petname; 
    private String breed; // 품종 이름
    private String gender;
    private LocalDate birthdate;
    private Boolean neuter;
    private Double weight;
}