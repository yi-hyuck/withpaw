package com.mysite.test.schedule;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.test.place.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleInstanceController {

    private final ScheduleInstanceService instanceService;

    // 특정 스케줄의 인스턴스 생성
    @PostMapping("/{id}/generate")
    public ResponseEntity<ApiResponse<List<ScheduleInstance>>> generate(@PathVariable("id") Long id) {
        List<ScheduleInstance> instances = instanceService.generateInstances(id);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "일정 인스턴스 생성 완료", instances));
    }

    // 특정 스케줄의 인스턴스 조회
    @GetMapping("/{id}/instances")
    public ResponseEntity<ApiResponse<List<ScheduleInstanceResponseDTO>>> getInstances(@PathVariable("id") Long id) {
        List<ScheduleInstance> instances = instanceService.getInstances(id);
        List<ScheduleInstanceResponseDTO> response = instances.stream()
                .map(ScheduleInstanceResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "일정 인스턴스 조회 성공", response));
    }

    // 특정 인스턴스 완료 처리
    @PutMapping("/instances/{instanceId}/complete")
    public ResponseEntity<ApiResponse<Void>> markCompleted(@PathVariable("instanceId") Long instanceId) {
        instanceService.markCompleted(instanceId);
        return ResponseEntity
                .ok(ApiResponse.success(HttpStatus.OK, "일정 인스턴스 완료 처리됨", null));
    }
}
