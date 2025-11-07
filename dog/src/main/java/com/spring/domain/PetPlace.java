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
@Table(name = "pet_place")
@Getter
@Setter
@NoArgsConstructor
public class PetPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;             // 장소 이름
    private String address;          // 주소
    private String phone;            // 전화번호
    private Double latitude;         // 위도
    private Double longitude;        // 경도

    @Enumerated(EnumType.STRING)
    private PlaceType type;          // 장소 유형 (병원, 식당, 카페 등)

    private Boolean petAllowed;      // 반려동물 동반 가능 여부
    private String description;      // 설명
    private String source;           // 데이터 출처 (예: KAKAO, CSV, USER)

}
