package com.mysite.test.pet;

import java.time.LocalDate;

import com.mysite.test.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Pet {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer petId; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member owner; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id", nullable = false)
    private Breed breed; 

    @Column(nullable = false)
    private String petname; 

    @Column(nullable = false)
    private LocalDate birthdate; 

    @Column(nullable = false)
    private String gender; 

    @Column(nullable = false)
    private Boolean neuter; 

    @Column(columnDefinition = "DECIMAL(5, 2)", nullable = false)
    private Double weight; 


	

}
