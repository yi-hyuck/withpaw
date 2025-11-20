package com.spring.dto;

import com.spring.domain.PetPlace;
import com.spring.domain.PlaceType;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetPlaceRequestDTO {

    @NotBlank(message = "장소 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    private String phone;

    @NotNull(message = "위도(latitude)는 필수입니다.")
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitude;

    @NotNull(message = "경도(longitude)는 필수입니다.")
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double longitude;

    @NotNull(message = "장소 유형은 필수입니다.")
    private PlaceType type;

    private Boolean petAllowed;

    private String description;

    public PetPlace toEntity() {
        PetPlace place = new PetPlace();
        place.setName(this.name);
        place.setAddress(this.address);
        place.setPhone(this.phone);
        place.setLatitude(this.latitude);
        place.setLongitude(this.longitude);
        place.setType(this.type);
        place.setPetAllowed(this.petAllowed);
        place.setDescription(this.description);
        return place;
    }
}
