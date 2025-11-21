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
    
    public void create(Member owner, String name, String species, String gender, 
                       LocalDate birthDate, Boolean neuter, Double weight) {
                       
        Breed breedEntity = findOrCreateBreed(species);
        
      
        Pet pet = new Pet();
        pet.setOwner(owner);
        pet.setBreed(breedEntity);
        pet.setPetname(name);
        pet.setGender(gender);
        pet.setBirthdate(birthDate);
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
    
    
    // 수정 기능
    @Transactional
    public void update(Pet pet, String name, LocalDate birthDate, Boolean neuter, Double weight) {
                       
        
        // 정보 업데이트
        pet.setPetname(name);
        pet.setBirthdate(birthDate);
        pet.setNeuter(neuter);
        pet.setWeight(weight);
    }
    

    // 삭제 기능
    public void delete(Pet pet) {
        this.petRepository.delete(pet);
    }
    
    
    

}
