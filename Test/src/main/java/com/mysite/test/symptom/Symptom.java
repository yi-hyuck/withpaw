package com.mysite.test.symptom;

import java.time.LocalDate;

import com.mysite.test.pet.Pet;

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
public class Symptom {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer symptomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet; 

    @Column(columnDefinition = "TEXT", nullable = false)
    private String symptom;

    @Column(nullable = false)
    private LocalDate symptomdate;

}
