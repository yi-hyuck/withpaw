package com.mysite.test.pet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.test.member.Member;

public interface PetRepository extends JpaRepository<Pet, Integer> {
}