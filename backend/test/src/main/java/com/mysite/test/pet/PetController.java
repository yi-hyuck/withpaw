package com.mysite.test.pet;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.test.DataNotFoundException;
import com.mysite.test.member.Member;
import com.mysite.test.member.MemberResponseDto;
import com.mysite.test.member.MemberService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/pet")
public class PetController {

    private final PetService petService;
    private final MemberService memberService;

    
    //품종 목록 조회
    @GetMapping("/breeds")
    public ResponseEntity<List<BreedDto>> getBreeds(){
    	List<Breed> breeds = petService.getAllBreeds();
    	List<BreedDto> breedDtos = breeds.stream()
    			.map(breed -> new BreedDto(breed.getBreedname()))
    			.collect(Collectors.toList());
    	
    	return ResponseEntity.ok(breedDtos);
    }
    
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
	            petForm.getPetname(),
	            petForm.getBreed(),
	            petForm.getGender(),
	            petForm.getBirthdate(),
	            petForm.getNeuter(),
	            petForm.getWeight()
        	);
        	
        	return new ResponseEntity<>(HttpStatus.CREATED);
        } catch(Exception e) {
        	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // 반려동물 수정 폼
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{petId}")
    // DTO를 PetForm으로 통일
    public ResponseEntity<?> getModifyPet(@PathVariable("petId") Integer petId, Principal principal) {
    	try{
        	Pet pet = this.petService.getPet(petId);
        	
        	if (!pet.getOwner().getLoginId().equals(principal.getName())) {
        		throw new DataNotFoundException("수정 권한이 없습니다.");
	        }
	
	        Map<String, Object> petData = new HashMap<>();
	        // 폼에 현재 펫 정보 채우기
	        petData.put("petId", pet.getPetId());
	        petData.put("petname", pet.getPetname());
	        petData.put("breed", pet.getBreed().getBreedname());
	        petData.put("gender", pet.getGender());
	        petData.put("birthdate", pet.getBirthdate());
	        petData.put("neuter", pet.getNeuter());
	        petData.put("weight", pet.getWeight());
	        
	        return ResponseEntity.ok(petData);
        } catch (DataNotFoundException e){
        	return ResponseEntity.status(404).body(Map.of("message", "반려동물을 찾을 수 없습니다."));
        }

        
    }


    // 반려동물 수정 처리 (품종/성별 제외)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{petId}")
    public ResponseEntity<?> modifyPet(@Valid @RequestBody PetForm petForm, BindingResult bindingResult, Principal principal, @PathVariable("petId") Integer petId) {
    	log.info("수정 요청 Pet ID: {}", petId);
    	log.info("수신된 PetForm 데이터 - petname: {}, breed: {}, gender: {}, birthdate: {}, neuter: {}, weight: {}", 
                petForm.getPetname(), 
                petForm.getBreed(), 
                petForm.getGender(), 
                petForm.getBirthdate(), 
                petForm.getNeuter(), 
                petForm.getWeight());
        
    	if (bindingResult.hasErrors()) {
	    	Map<String, Object> errors = new HashMap<>();
	    	bindingResult.getFieldErrors().forEach(error ->
            	errors.put(error.getField(), error.getDefaultMessage())
	        );
	        return ResponseEntity.badRequest().body(Map.of("message", "입력값 오류", "errors", errors));
	    }
	    try{
	    	Pet pet = this.petService.getPet(petId);
	    	
	    	if (!pet.getOwner().getLoginId().equals(principal.getName())) {
	    		throw new DataNotFoundException("수정 권한이 없습니다.");
	    	}
	    	
	    	this.petService.update(
			    pet,
			    petForm.getPetname(),
			    petForm.getBirthdate(),
			    petForm.getNeuter(),
			    petForm.getWeight(),
			    petForm.getBreed(),
			    petForm.getGender()
		    );
		    return ResponseEntity.ok(Map.of("message", "반려동물 정보 수정"));
	    } catch (DataNotFoundException e) {
	    	return ResponseEntity.status(404).body(Map.of("message", "반려동물을 찾을 수 없습니다."));
	    }
    }
    
//    @PreAuthorize("isAuthenticated()")
//    @PutMapping("/modify/{petId}")
//    public ResponseEntity<?> modifyPet(@Valid @RequestBody PetDto petDto, BindingResult bindingResult, Principal principal, @PathVariable("petId") Integer petId) {
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
//	            petDto
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
    public ResponseEntity<PetDto> getPetDetail(@PathVariable("petId") Integer petId, Principal principal) {
        try {
            Pet pet = this.petService.getPet(petId);

            if (!pet.getOwner().getLoginId().equals(principal.getName())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            PetDto petDto = petService.getPetDetail(petId);
            return ResponseEntity.ok(petDto);
            
        } catch (DataNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}