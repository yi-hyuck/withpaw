package com.spring.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.spring.domain.PetPlace;
import com.spring.domain.PlaceType;

@Repository
public interface PetPlaceRepository extends JpaRepository<PetPlace, Long> {

    List<PetPlace> findByType(PlaceType type);
    List<PetPlace> findByPetAllowedTrue();
    List<PetPlace> findByTypeAndPetAllowedTrue(PlaceType type);
}
