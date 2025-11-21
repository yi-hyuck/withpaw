package com.spring.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.domain.Symptom;


@Repository
public interface SymptomRepository extends JpaRepository<Symptom, Long> {
    List<Symptom> findAllByMemberId(Long memberId);
    

}