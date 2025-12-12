package com.mysite.test.pet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.test.member.Member;

public interface PetRepository extends JpaRepository<Pet, Integer> {
	
	// Member 객체를 통해 해당 Member가 소유한 Pet 목록을 조회(챗봇)
    List<Pet> findByOwner(Member owner);

}
