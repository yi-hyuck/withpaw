package com.spring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.domain.Schedule;


@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findAllByMemberId(Long memberId);
//@Repository
//public class ScheduleRepository {
//
//	private static final Map<Long, Schedule> store = new HashMap<>();
//	private static final AtomicLong sequence = new AtomicLong(0L);
//
//	// 저장 (id 자동 생성)
//	public Schedule save(Schedule schedule) {
//		if (schedule.getId() == null) {
//			schedule.setId(sequence.incrementAndGet());
//		}
//		store.put(schedule.getId(), schedule);
//		return schedule;
//	}
//
//	// 특정 회원의 전체 일정 조회
//	public List<Schedule> findAllByMemberId(Long memberId) {
//		return store.values().stream().filter(s -> s.getMemberId().equals(memberId)).toList();
//	}
//
////    // 특정 회원 + 반려동물 ID로 일정 조회
////    public List<Schedule> findAllByMemberIdAndPetId(Long memberId, Long petId) {
////        return store.values().stream()
////                .filter(s -> s.getMemberId().equals(memberId) && s.getPetId().equals(petId))
////                .toList();
////    }
//
//	// id로 단일 일정 조회
//	public Optional<Schedule> findById(Long id) {
//		return Optional.ofNullable(store.get(id));
//	}
//
//	// 삭제
//	public void deleteById(Long id) {
//		store.remove(id);
//	}
//	
//	//존재여부확인
//	public boolean existsById(Long id) {
//	    return store.containsKey(id);
//	}
}
