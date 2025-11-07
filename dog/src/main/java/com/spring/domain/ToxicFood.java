package com.spring.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "toxicFood")
public class ToxicFood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name; //식품명
	 @Enumerated(EnumType.STRING)
	private ToxicFoodCategory category;//분류(초콜릿,견과류,과일류 등등)
//	private ToxicIngredient ingredient;//성분명
//	private String additionalIngredient;//기타성분(텍스트)
	 @Enumerated(EnumType.STRING)
	private ToxicityLevel toxicityLevel;//독성등급(안전, 위험, 치명적)
	private String description;//설명(객관적정보)
//	private String symptoms;//섭취 시 나타날 수 있는 증상들
//	private String safeDose;//섭취한도기준
//	private String imageUrl;//이미지
	private String note;//참고, 예외, 출처(ASPCA)
	
}
