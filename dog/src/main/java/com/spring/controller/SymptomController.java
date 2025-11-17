package com.spring.controller;

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
import com.spring.dto.SymptomRequestDTO;
import com.spring.dto.SymptomResponseDTO;
import com.spring.service.SymptomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/symptoms")
@RequiredArgsConstructor
public class SymptomController {

    private final SymptomService symptomService;

    // 증상 기록 등록
    @PostMapping
    public ResponseEntity<ApiResponse<SymptomResponseDTO>> create(@Valid @RequestBody SymptomRequestDTO dto) {
        SymptomResponseDTO result = symptomService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "증상 기록 등록 성공", result));
    }

//    // 특정 회원 + 반려동물 증상 조회
//    @GetMapping("/member/{memberId}/pet/{petId}")
//    public ResponseEntity<ApiResponse<List<SymptomResponseDTO>>> getByMemberAndPet(
//            @PathVariable Long memberId,
//            @PathVariable Long petId) {
//
//        List<SymptomResponseDTO> list = symptomService.getByMemberAndPet(memberId, petId);
//        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "회원+반려동물 증상 기록 조회 성공", list));
//    }
    
    // 특정 회원의 모든 증상 기록 조회
    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<SymptomResponseDTO>>> getByMember(@PathVariable("memberId") Long memberId) {
        List<SymptomResponseDTO> list = symptomService.getByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "회원 전체 증상 기록 조회 성공", list));
    }

    // 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SymptomResponseDTO>> getById(@PathVariable("id") Long id) {
        SymptomResponseDTO result = symptomService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "단일 증상 기록 조회 성공", result));
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SymptomResponseDTO>> update(@PathVariable("id") Long id,
                                                                  @Valid @RequestBody SymptomRequestDTO dto) {
        SymptomResponseDTO result = symptomService.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "증상 기록 수정 성공", result));
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
        symptomService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "증상 기록 삭제 성공", null));
    }
}
