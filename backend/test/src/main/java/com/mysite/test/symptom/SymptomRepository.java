package com.mysite.test.symptom;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SymptomRepository extends JpaRepository<Symptom, Long> {
    List<Symptom> findAllByMemberId(Long memberId);
    

}