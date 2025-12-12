package com.mysite.test.member;

import java.util.List;
import java.util.stream.Collectors;

import com.mysite.test.pet.PetDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponseDto {
    private Integer userId;
    private String loginId; 
    private String email; 
    private List<PetDto> pets; // 반려동물 목록을 위한 내부 DTO

    // 정적 팩토리 메서드: Member 엔티티를 DTO로 변환
    public static MemberResponseDto from(Member member) {
        // 반려동물 정보도 PetDto 리스트로 변환
        List<PetDto> petDtos = member.getPets().stream()
            .map(pet -> PetDto.builder()
                .petId(pet.getPetId())
                .petname(pet.getPetname())
                .breed(pet.getBreed().getBreedname()) // Breed 엔티티에서 이름 가져오기
                .gender(pet.getGender())
                .birthdate(pet.getBirthdate())
                .neuter(pet.getNeuter())
                .weight(pet.getWeight())
                .build())
            .collect(Collectors.toList());

        return MemberResponseDto.builder()
            .userId(member.getUserId())
            .loginId(member.getLoginId())
            .email(member.getEmail())
            .pets(petDtos)
            .build();
    }
    
    // 반려동물 정보를 담기 위한 내부 DTO
//    @Getter
//    @Setter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class PetDto {
//        private Integer petId;
//        private String name; 
//        private String breed; // 품종 이름
//        private String gender;
//        private LocalDate birthDate;
//        private Boolean neuter;
//        private Double weight;
//    }
}