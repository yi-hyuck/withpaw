package com.mysite.test.pet;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.test.DataNotFoundException;
import com.mysite.test.member.Member;
import com.mysite.test.member.MemberResponseDto;
import com.mysite.test.member.MemberService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pet")
public class PetController {

    private final PetService petService;
    private final MemberService memberService;

    
    // 반려동물 목록 조회 및 관리 페이지
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/manage")
    public ResponseEntity<MemberResponseDto> managePets(Principal principal) {
    	if(principal == null) {
    		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    	}
    	try {
    		Member member = memberService.getMember(principal.getName());
    		MemberResponseDto responseDto = MemberResponseDto.from(member);
    		return ResponseEntity.ok(responseDto);
    	} catch(DataNotFoundException e) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    }

    // 반려동물 추가 처리
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<?> createPet(@Valid @RequestBody PetForm petForm, Principal principal) {
        Member owner = memberService.getMember(principal.getName());
        
        try {
        	petService.create(
	            owner, 
	            petForm.getName(),
	            petForm.getBreed(),
	            petForm.getGender(),
	            petForm.getBirthDate(),
	            petForm.getNeuter(),
	            petForm.getWeight()
        	);
        	
        	return new ResponseEntity<>(HttpStatus.CREATED);
        } catch(Exception e) {
        	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    // 반려동물 수정 폼
//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/modify/{petId}")
//    // DTO를 PetForm으로 통일
//    public String modifyPet(PetForm petForm, @PathVariable("petId") Integer petId, 
//                            Principal principal) {
//        Pet pet = this.petService.getPet(petId);
//
//        if (!pet.getOwner().getLoginId().equals(principal.getName())) {
//            throw new DataNotFoundException("수정 권한이 없습니다.");
//        }
//
//        // 폼에 현재 펫 정보 채우기
//        petForm.setPetId(pet.getPetId()); 
//        petForm.setName(pet.getPetname());
//        petForm.setBreed(pet.getBreed().getBreedname()); 
//        petForm.setGender(pet.getGender());
//        petForm.setBirthDate(pet.getBirthdate());
//        petForm.setNeuter(pet.getNeuter());
//        petForm.setWeight(pet.getWeight());
//        
//        return "pet_form";
//    }


    // 반려동물 수정 처리 (품종/성별 제외)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{petId}")
    public ResponseEntity<?> modifyPet(@Valid @RequestBody PetUpdateForm petUpdateForm, BindingResult bindingResult, Principal principal, @PathVariable("petId") Integer petId) {
    	if (bindingResult.hasErrors()) {
            return new ResponseEntity<>("유효성 검사 실패", HttpStatus.BAD_REQUEST);
        }
    	
    	try {
            Pet pet = this.petService.getPet(petId);
            
            if (!pet.getOwner().getLoginId().equals(principal.getName())) {
            throw new DataNotFoundException("수정 권한이 없습니다.");
            }
            
            this.petService.updatePetInfo(pet, petUpdateForm);
            
            
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataNotFoundException e) {
        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
        	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
//    @PreAuthorize("isAuthenticated()")
//    @PutMapping("/modify/{petId}")
//    public ResponseEntity<?> modifyPet(@Valid @RequestBody PetUpdateForm petUpdateForm, BindingResult bindingResult, Principal principal, @PathVariable("petId") Integer petId) {
//    	if (bindingResult.hasErrors()) {
//            return new ResponseEntity<>("유효성 검사 실패", HttpStatus.BAD_REQUEST);
//        }
//    	
//    	try {
//            Pet pet = this.petService.getPet(petId);
//            
//            if (!pet.getOwner().getLoginId().equals(principal.getName())) {
//            throw new DataNotFoundException("수정 권한이 없습니다.");
//            }
//            
//            this.petService.update(
//	            pet,
//	            petUpdateForm
//            );
//            
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (DataNotFoundException e) {
//        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (Exception e) {
//        	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }


    // 반려동물 삭제 처리
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete/{petId}")
    public ResponseEntity<?> deletePet(@PathVariable("petId") Integer petId, Principal principal) {
        try{
        	Pet pet = this.petService.getPet(petId);
        	if (!pet.getOwner().getLoginId().equals(principal.getName())) {
        		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        	}
        	
        	this.petService.delete(pet);
        	
        	return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch(DataNotFoundException e) {
        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(Exception e) {
        	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //특정 반려동물 상세 정보 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/detail/{petId}")
    public ResponseEntity<MemberResponseDto.PetDto> getPetDetail(@PathVariable("petId") Integer petId, Principal principal) {
        try {
            Pet pet = this.petService.getPet(petId);

            if (!pet.getOwner().getLoginId().equals(principal.getName())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            MemberResponseDto.PetDto petDto = petService.getPetDetail(petId);
            return ResponseEntity.ok(petDto);
            
        } catch (DataNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}