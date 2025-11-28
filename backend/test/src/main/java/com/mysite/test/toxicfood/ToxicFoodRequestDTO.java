package com.mysite.test.toxicfood;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToxicFoodRequestDTO {
	@NotBlank(message="식품 이름은 필수입니다.")
	@Size(max = 100, message = "식품 이름은 100자 이내로 입력하세요.")
    private String name; //이름
	@NotNull(message="식품 카테고리를 지정해야 합니다.")
    private ToxicFoodCategory category; //분류
    //private ToxicIngredient ingredient; //성분명
    //private String additionalIngredient; //기타성분(텍스트
	@NotNull(message="독성 수준을 선택해야 합니다.")
    private ToxicityLevel toxicityLevel; //위험도레벨
	@Size(max=1000, message="1000자가 최대입니다.")
	@NotBlank(message = "식품 설명은 필수입니다.")
    private String description; //설명
    //private String symptoms; //증상
    //private String safeDose; //한도기준
    //private String imageUrl;
	@Size(max=200, message="200자가 최대입니다.")
    private String note; //부가설명
}
