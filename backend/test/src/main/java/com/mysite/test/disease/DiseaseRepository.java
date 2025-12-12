package com.mysite.test.disease;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    //relatedSymptomIds 컬럼에 특정 증상 ID 문자열이 포함되어 있는 질병 검색
	@Query("SELECT d FROM Disease d WHERE :symptomId MEMBER OF d.relatedSymptomIds")
	List<Disease> findBySymptomIds(@Param("symptomId") Long symptomId);


//@Repository
//public class DiseaseRepository {
//	private static final Map<Long, Disease> store = new HashMap<>();
//    private static final AtomicLong sequence = new AtomicLong(0L);
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//    
//    public Disease save(Disease disease) {
//        if (disease.getId() == null)
//            disease.setId(sequence.incrementAndGet());
//        store.put(disease.getId(), disease);
//        return disease;
//    }
//
//    public List<Disease> findAll() {
//        return new ArrayList<>(store.values());
//    }
//
//    // 증상 ID로 관련 질병 찾기
//    public List<Disease> findBySymptomIds(List<Long> symptomIds) {
//        List<Disease> result = new ArrayList<>();
//
//        for (Disease disease : store.values()) {
//            try {
//                List<Long> relatedIds = objectMapper.readValue(
//                        disease.getRelatedSymptomIds(),
//                        new TypeReference<List<Long>>() {}
//                );
//
//                // 증상 ID 중 하나라도 포함되어 있으면 결과에 추가
//                for (Long sId : symptomIds) {
//                    if (relatedIds.contains(sId)) {
//                        result.add(disease);
//                        break;
//                    }
//                }
//            } catch (Exception e) {
//            }
//        }
//
//        return result;
//    }
//    // 초기 데이터 세팅용
//    public void initData() {
//        Disease d1 = new Disease();
//        d1.setName("장염");
//        d1.setDescription("구토 증상과 관련 있음");
//        d1.setRelatedSymptomIds(jsonList(List.of(1L)));
//
//        Disease d2 = new Disease();
//        d2.setName("감기");
//        d2.setDescription("기침 증상과 관련 있음");
//        d2.setRelatedSymptomIds(jsonList(List.of(2L)));
//
//        save(d1);
//        save(d2);
//    }
//    
//    private String jsonList(List<Long> list) {
//        try {
//            return objectMapper.writeValueAsString(list);
//        } catch (Exception e) {
//            return "[]";
//        }
//    }
}