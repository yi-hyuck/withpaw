package com.mysite.test.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeviceTokenRequestDTO {
    @NotNull @Positive
    private Long memberId;
    @NotBlank
    private String token;
    private Boolean enabled;
}