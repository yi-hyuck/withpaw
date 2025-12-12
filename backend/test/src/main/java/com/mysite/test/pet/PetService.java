package com.mysite.test.pet;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.test.DataNotFoundException;
import com.mysite.test.member.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetService {
	private final PetRepository petRepository;
    private final BreedRepository breedRepository; 
    
    public PetDto getPetDetail(Integer petId){
    	Pet pet = this.getPet(petId);
    	
    	return PetDto.builder()
    			.petId(pet.getPetId())
    			.petname(pet.getPetname())
    			.breed(pet.getBreed().getBreedname())
    			.gender(pet.getGender())
    			.birthdate(pet.getBirthdate())
    			.neuter(pet.getNeuter())
    			.weight(pet.getWeight())
    			.build();
    }

    public List<Breed> getAllBreeds(){
    	return breedRepository.findAll();
    }
    
    @Transactional
    private Breed findBreed(String breedName) {
        Optional<Breed> optionalBreed = breedRepository.findByBreedname(breedName);
        
        return optionalBreed.get();
    }
    
    
    @Transactional
    public void create(Member owner, String petname, String breed, String gender, 
                       LocalDate birthdate, Boolean neuter, Double weight) {
                       
        Breed breedEntity = findBreed(breed);
      
        Pet pet = new Pet();
        pet.setOwner(owner);
        pet.setBreed(breedEntity);
        pet.setPetname(petname);
        pet.setGender(gender);
        pet.setBirthdate(birthdate);
        pet.setNeuter(neuter);
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
    
    
//     수정 기능
    @Transactional
    public Pet update(Pet pet, String petname, LocalDate birthdate, Boolean neuter, Double weight, String breed, String gender) {
    	Breed breedEntity = findBreed(breed);
    	
    	pet.setPetname(petname);
        pet.setBirthdate(birthdate);
        pet.setNeuter(neuter);
        pet.setWeight(weight);
        pet.setBreed(breedEntity);
        pet.setGender(gender);
        
        return this.petRepository.save(pet);
    }
    
//    @Transactional
//    public Pet updatePetInfo(Pet pet, Map<String, Object> requestMap) {
//    	if (requestMap.containsKey("petname")) {
//            String newName = (String) requestMap.get("petname");
//            if (newName != null && !newName.isBlank()) {
//                pet.setPetname(newName);
//            } else {
//                throw new IllegalArgumentException("이름은 필수 항목이며 비워둘 수 없습니다.");
//            }
//        }
//    	if (requestMap.containsKey("birthDate")) {
//            String dateString = (String) requestMap.get("birthDate");
//            try {
//                LocalDate newBirthDate = LocalDate.parse(dateString);
//                if (newBirthDate.isAfter(LocalDate.now())) {
//                    throw new IllegalArgumentException("생년월일은 미래 날짜일 수 없습니다.");
//                }
//                pet.setBirthDate(newBirthDate);
//            } catch (Exception e) {
//                 throw new IllegalArgumentException("유효하지 않은 생년월일 형식입니다. (YYYY-MM-DD)");
//            }
//        }
//    	if (requestMap.containsKey("neuter")) {
//            Boolean newNeuter = (Boolean) requestMap.get("neuter");
//            if (newNeuter != null) {
//                pet.setNeuter(newNeuter);
//            } else {
//                throw new IllegalArgumentException("중성화 여부는 필수 항목입니다.");
//            }
//        }
//    	if (requestMap.containsKey("weight")) {
//            Number weightNumber = (Number) requestMap.get("weight");
//            if (weightNumber != null) {
//                Double newWeight = weightNumber.doubleValue();
//                if (newWeight < 0.1) {
//                    throw new IllegalArgumentException("몸무게는 0.1 kg 이상이어야 합니다.");
//                }
//                pet.setWeight(newWeight);
//            } else {
//                 throw new IllegalArgumentException("몸무게는 필수 항목입니다.");
//            }
//        }
//    	return this.petRepository.save(pet);
//    }
    
    
    
    
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
}
