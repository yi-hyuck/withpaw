package com.spring.dto;

import com.spring.domain.ToxicFood;
import com.spring.domain.ToxicFoodCategory;
import com.spring.domain.ToxicityLevel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ToxicFoodResponseDTO {
    private Long id;
    private String name;
    private ToxicFoodCategory category;
    //private ToxicIngredient ingredient;
   // private String additionalIngredient;
    private ToxicityLevel toxicityLevel;
    private String description;
//    private String symptoms;
//    private String safeDose;
//    private String imageUrl;
    private String note;

    public static ToxicFoodResponseDTO fromEntity(ToxicFood toxicFood) {
        return new ToxicFoodResponseDTO(
                toxicFood.getId(),
                toxicFood.getName(),
                toxicFood.getCategory(),
//                toxicFood.getIngredient(),
//                toxicFood.getAdditionalIngredient(),
                toxicFood.getToxicityLevel(),
                toxicFood.getDescription(),
//                toxicFood.getSymptoms(),
//                toxicFood.getSafeDose(),
//                toxicFood.getImageUrl(),
                toxicFood.getNote()
        );
    }
}
