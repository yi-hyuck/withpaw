package com.spring.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "symptom")
public class Symptom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 증상기록 ID
	@NotNull
	private Long memberId; // 회원id
//	@NotNull
//	private Long petId; // 반려동물 ID
	private LocalDateTime symptomDate; // 증상 기록 날짜
	@Column(length = 2000)
	private String description; // 기록(텍스트)
	//private String selectedSymptomIds; // 증상ID리스트(충혈,구토 등의ID
	//private String suspectedDiseaseIds; // 연관 의심질병ID
	// private List<Disease> suspectedDiseases = new ArrayList<>();
	private LocalDateTime createdAt; // 작성시점..
	
	
    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

}
