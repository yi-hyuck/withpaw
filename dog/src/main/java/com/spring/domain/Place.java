package com.spring.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name; // 장소명
	private String address; // 주소
	private String roadAddress; // 도로명 주소
	private String phone; // 전화번호
	private String category; // 카테고리명)
	private Double latitude; // 위도
	private Double longitude; // 경도
	private String placeUrl; // 카카오맵 URL
	private String source; // 데이터 출처
}
