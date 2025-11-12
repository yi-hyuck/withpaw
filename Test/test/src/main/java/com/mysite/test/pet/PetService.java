package com.mysite.test.pet;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
