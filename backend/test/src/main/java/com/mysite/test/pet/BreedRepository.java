package com.mysite.test.pet;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BreedRepository extends JpaRepository<Breed, Integer> {
    Optional<Breed> findByBreedname(String breedname);

}
