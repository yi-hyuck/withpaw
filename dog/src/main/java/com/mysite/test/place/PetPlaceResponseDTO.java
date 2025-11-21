package com.mysite.test.place;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PetPlaceResponseDTO {

	private Long id; // 장소 ID
	private String name; // 장소 이름
	private String address; // 주소
	private String phone; // 전화번호
	private Double latitude; // 위도
	private Double longitude; // 경도
	private PlaceType type; // 장소 유형 (카페, 병원 등)
	private Boolean petAllowed; // 반려동물 동반 가능 여부
	private String description; // 설명
	private String source; // 데이터 출처 (예: KAKAO, USER)

	public static PetPlaceResponseDTO fromEntity(PetPlace place) {
		PetPlaceResponseDTO dto = new PetPlaceResponseDTO();
		dto.setId(place.getId());
		dto.setName(place.getName());
		dto.setAddress(place.getAddress());
		dto.setPhone(place.getPhone());
		dto.setLatitude(place.getLatitude());
		dto.setLongitude(place.getLongitude());
		dto.setType(place.getType());
		dto.setPetAllowed(place.getPetAllowed());
		dto.setDescription(place.getDescription());
		dto.setSource(place.getSource());
		return dto;
	}
}
