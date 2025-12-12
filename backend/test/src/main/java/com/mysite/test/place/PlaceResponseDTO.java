package com.mysite.test.place;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceResponseDTO {
    private String name;          // 장소명
    private String address;       // 지번 주소
    private String roadAddress;   // 도로명 주소
    private String phone;         // 전화번호
    private Double latitude;      // 위도 (y)
    private Double longitude;     // 경도 (x)
    private String url;           // 장소 상세 URL
}
