package com.mysite.test.member;

import java.util.List;

import com.mysite.test.pet.Pet;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Member { 
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId; 

    @Column(unique = true, nullable = false)
    private String loginId; 

    @Column(unique = true, nullable = false)
    private String email; 

    @Column(nullable = false)
    private String password; 
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Pet> pets;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;
	

}