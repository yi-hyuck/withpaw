package com.mysite.test.schedule;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeviceTokenResponseDTO {
    private Long id;
    private Long memberId;
    private String token;
    private Boolean enabled;

    public static DeviceTokenResponseDTO fromEntity(DeviceToken e) {
        DeviceTokenResponseDTO dto = new DeviceTokenResponseDTO();
        dto.id = e.getId();
        dto.memberId = e.getMemberId();
        dto.token = e.getToken();
        dto.enabled = e.getEnabled();
        return dto;
    }
}