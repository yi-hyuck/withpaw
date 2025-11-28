package com.mysite.test.place;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetPlaceRepository extends JpaRepository<PetPlace, Long> {

    List<PetPlace> findByType(PlaceType type);
    List<PetPlace> findByPetAllowedTrue();
    List<PetPlace> findByTypeAndPetAllowedTrue(PlaceType type);
}
