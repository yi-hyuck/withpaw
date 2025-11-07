package com.spring.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.common.ApiResponse;
import com.spring.domain.ReminderLog;
import com.spring.dto.ReminderLogResponseDTO;
import com.spring.repository.ReminderLogRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderLogRepository reminderLogRepository;

    // 전체 리마인더 로그 조회
    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<List<ReminderLogResponseDTO>>> getAllLogs() {
        List<ReminderLog> logs = reminderLogRepository.findAll();
        List<ReminderLogResponseDTO> dtoList = logs.stream()
                .map(ReminderLogResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(
            ApiResponse.success(HttpStatus.OK, "리마인더 로그 전체 조회 성공", dtoList)
        );
    }

    // 리마인더 로그 삭제 (오래된 로그 정리용)
    @DeleteMapping("/logs/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLog(@PathVariable Long id) {
        reminderLogRepository.deleteById(id);
        return ResponseEntity.ok(
            ApiResponse.success(HttpStatus.OK, "리마인더 로그 삭제 완료", null)
        );
    }
}
