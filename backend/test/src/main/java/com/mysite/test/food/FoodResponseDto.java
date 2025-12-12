package com.mysite.test.food;

import lombok.Getter;

@Getter
public class FoodResponseDto {
    private Long id;
    private String name;
    private boolean edible;
    private String description;
    private String cautions;

    public FoodResponseDto(Food entity) {
        
    	this.id = entity.getId();
        this.name = entity.getName();
        this.edible = entity.isEdible();
        this.description = entity.getDescription();
        this.cautions = entity.getCautions();

    }
}
