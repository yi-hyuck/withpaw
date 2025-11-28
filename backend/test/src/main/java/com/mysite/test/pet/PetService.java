package com.mysite.test.pet;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.test.DataNotFoundException;
import com.mysite.test.member.Member;
import com.mysite.test.member.MemberResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetService {
	private final PetRepository petRepository;
    private final BreedRepository breedRepository; 

    @Transactional
    private Breed findOrCreateBreed(String breedName) {
        Optional<Breed> optionalBreed = breedRepository.findByBreedname(breedName);
        
        if (optionalBreed.isPresent()) {
            return optionalBreed.get();
        }
        
        Breed newBreed = new Breed();
        newBreed.setBreedname(breedName);
        return breedRepository.save(newBreed);
    }
    
    public void create(Member owner, String name, String breed, String gender, 
                       LocalDate birthDate, Integer neuter, Double weight) {
                       
        Breed breedEntity = findOrCreateBreed(breed);
        Boolean isNeuter = neuter != null && neuter == 1;
      
        Pet pet = new Pet();
        pet.setOwner(owner);
        pet.setBreed(breedEntity);
        pet.setPetname(name);
        pet.setGender(gender);
        pet.setBirthdate(birthDate);
        pet.setNeuter(isNeuter);
        pet.setWeight(weight);
        this.petRepository.save(pet);
    }
    
 // Member 객체를 이용해 반려동물 목록을 조회하는 메서드(챗봇)
    public List<Pet> getPetsByMember(Member owner) {
        return petRepository.findByOwner(owner);
    }
    
    
    
    // 펫 ID로 단일 펫 조회
    public Pet getPet(Integer petId) {
        Optional<Pet> pet = this.petRepository.findById(petId);
        if (pet.isPresent()) {
            return pet.get();
        } else {
            // 해당 ID의 펫이 없을 경우 예외 처리
            throw new DataNotFoundException("pet not found");
        }
    }
    
    
    // 수정 기능
    @Transactional
    public void updatePetInfo(Pet pet, PetUpdateForm petUpdateForm) {
    	if(petUpdateForm.getPetname() != null && !pet.getPetname().equals(petUpdateForm.getPetname())) {
    		pet.setPetname(petUpdateForm.getPetname());
        }
        
        if(petUpdateForm.getBirthDate() != null && !pet.getBirthdate().isEqual(petUpdateForm.getBirthDate())) {
        	pet.setBirthdate(petUpdateForm.getBirthDate());
        }
        
        if(petUpdateForm.getNeuter() != null && pet.getNeuter() != petUpdateForm.getNeuter()) {
        	pet.setNeuter(petUpdateForm.getNeuter());
        }
        
        if(petUpdateForm.getWeight() != null && pet.getWeight() != petUpdateForm.getWeight()) {
        	pet.setWeight(petUpdateForm.getWeight());
        }
    }
//    public void updateName(Pet pet, String newName) {
//    	pet.setPetname(newName);
//    }
//    
//    @Transactional
//    public void updateBirth(Pet pet, LocalDate birthDate) {
//    	pet.setBirthdate(birthDate);
//    }
//    
//    @Transactional
//    public void updateNeuter(Pet pet, Boolean neuter ) {
//    	pet.setNeuter(neuter);
//    }
//    
//    @Transactional
//    public void updateWeight(Pet pet, Double weight ) {
//    	pet.setWeight(weight);
//    }
//    public void update(Pet pet, PetUpdateForm petUpdateForm) {
//    	String name = petUpdateForm.getPetname();
//    	LocalDate birthDate = petUpdateForm.getBirthDate();
//    	Integer neuter = petUpdateForm.getNeuter();
//    	Double weight = petUpdateForm.getWeight();
//    	
//        Boolean isNeuter = neuter != null && neuter == 1;
//        
//        // 정보 업데이트
//        pet.setPetname(name);
//        pet.setBirthdate(birthDate);
//        pet.setNeuter(isNeuter);
//        pet.setWeight(weight);
//    }
    

    // 삭제 기능
    public void delete(Pet pet) {
        this.petRepository.delete(pet);
    }
    
    public MemberResponseDto.PetDto getPetDetail(Integer petId){
    	Pet pet = this.getPet(petId);
    	
    	return MemberResponseDto.PetDto.builder()
    			.petId(pet.getPetId())
    			.name(pet.getPetname())
    			.breed(pet.getBreed().getBreedname())
    			.gender(pet.getGender())
    			.birthDate(pet.getBirthdate())
    			.neuter(pet.getNeuter())
    			.weight(pet.getWeight())
    			.build();
    }
}
