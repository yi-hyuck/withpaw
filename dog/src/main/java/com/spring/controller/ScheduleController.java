package com.spring.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.common.ApiResponse;
import com.spring.domain.Schedule;
import com.spring.dto.ScheduleRequestDTO;
import com.spring.dto.ScheduleResponseDTO;
import com.spring.dto.ScheduleUpdateDTO;
import com.spring.exception.NotFoundException;
import com.spring.repository.ScheduleRepository;
import com.spring.service.ScheduleInstanceService;
import com.spring.service.ScheduleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

	private final ScheduleService scheduleService;
	private final ScheduleRepository scheduleRepository;
	private final ScheduleInstanceService scheduleInstanceService;
	
	// 일정 등록
	@PostMapping
	public ResponseEntity<ApiResponse<ScheduleResponseDTO>> create(@Valid @RequestBody ScheduleRequestDTO dto) {
		ScheduleResponseDTO result = scheduleService.create(dto);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(HttpStatus.CREATED, "일정 등록 성공", result));
	}

	// 일정 수정
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<ScheduleResponseDTO>> update(@PathVariable("id") Long id,
			@RequestBody ScheduleUpdateDTO dto) {

		ScheduleResponseDTO result = scheduleService.update(id, dto);
		
	    Schedule schedule = scheduleRepository.findById(id)
	            .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다."));
	    scheduleInstanceService.regenerateInstances(schedule);
	    
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "일정 수정 성공", result));
	}

	// 일정 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
		scheduleService.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "일정 삭제 성공", null));
	}

	// 회원별 일정 조회
	@GetMapping("/member/{memberId}")
	public ResponseEntity<ApiResponse<List<ScheduleResponseDTO>>> getByMember(@PathVariable("memberId") Long memberId) {
		List<ScheduleResponseDTO> list = scheduleService.getByMemberId(memberId);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "회원 일정 조회 성공", list));
	}

	// 단일 일정 조회
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<ScheduleResponseDTO>> getById(@PathVariable("id") Long id) {
		ScheduleResponseDTO result = scheduleService.getById(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "단일 일정 조회 성공", result));
	}

	// 반복 일정 발생일 미리보기
	@GetMapping("/{id}/occurrences")
	public ResponseEntity<ApiResponse<List<LocalDateTime>>> getOccurrences(@PathVariable("id") Long id) {
		Schedule schedule = scheduleRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("해당 일정이 존재하지 않습니다."));
		List<LocalDateTime> occurrences = scheduleService.generateOccurrences(schedule);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "반복 일정 발생일 계산 성공", occurrences));
	}
}
