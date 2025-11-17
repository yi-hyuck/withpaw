package com.spring.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.domain.Symptom;


@Repository
public interface SymptomRepository extends JpaRepository<Symptom, Long> {
    //List<Symptom> findAllByPetId(Long petId);
    List<Symptom> findAllByMemberId(Long memberId);
    List<Symptom> findAllByMemberIdAndPetId(Long memberId, Long petId);

//@Repository
//public class SymptomRepository {
//    private static final Map<Long, Symptom> store = new HashMap<>();
//    private static final AtomicLong sequence = new AtomicLong(0L);
//
//    //저장
//    public Symptom save(Symptom symptom) {
//        if (symptom.getId() == null)
//            symptom.setId(sequence.incrementAndGet());
//        store.put(symptom.getId(), symptom);
//        return symptom;
//    }
//
////    // 회원 ID로 전체 증상 기록 조회
////    public List<Symptom> findAllByMemberId(Long memberId) {
////        return store.values().stream()
////                .filter(s -> s.getMemberId().equals(memberId))
////                .toList();
////    }
////    
////    // 회원 + 반려동물 기준으로 조회
////    public List<Symptom> findAllByMemberIdAndPetId(Long memberId, Long petId) {
////        return store.values().stream()
////                .filter(s -> s.getMemberId().equals(memberId) && s.getPetId().equals(petId))
////                .toList();
////    }
//    
//    
//    public List<Symptom> findAllByPetId(Long petId) {
//        return store.values().stream()
//                .filter(s -> s.getPetId().equals(petId))
//                .toList();
//    }
//
//    public Optional<Symptom> findById(Long id) {
//        return Optional.ofNullable(store.get(id));
//    }
//
//    public void deleteById(Long id) {
//        store.remove(id);
//    }
//    
//	public boolean existsById(Long id) {
//	    return store.containsKey(id);
//	}
}