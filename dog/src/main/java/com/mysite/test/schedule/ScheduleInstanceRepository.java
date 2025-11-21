package com.mysite.test.schedule;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleInstanceRepository extends JpaRepository<ScheduleInstance, Long> {
    void deleteAllByScheduleId(Long scheduleId);
    List<ScheduleInstance> findAllByScheduleId(Long scheduleId);
    List<ScheduleInstance> findByCompletedFalseAndOccurrenceTimeBefore(LocalDateTime time);
    @Query("""
    	    SELECT si 
    	    FROM ScheduleInstance si 
    	    JOIN FETCH si.schedule s 
    	    WHERE si.occurrenceTime BETWEEN :start AND :end
    	""")
    	List<ScheduleInstance> findWithScheduleByOccurrenceTimeBetween(
    	        @Param("start") LocalDateTime start,
    	        @Param("end") LocalDateTime end);


}
