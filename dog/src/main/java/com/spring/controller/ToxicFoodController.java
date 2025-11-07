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
import com.spring.dto.ToxicFoodRequestDTO;
import com.spring.dto.ToxicFoodResponseDTO;
import com.spring.service.ToxicFoodService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/toxic-foods")
@RequiredArgsConstructor
public class ToxicFoodController {

    private final ToxicFoodService toxicFoodService;

    // 등록
    @PostMapping
    public ResponseEntity<ApiResponse<ToxicFoodResponseDTO>> create(@Valid @RequestBody ToxicFoodRequestDTO dto) {
        ToxicFoodResponseDTO result = toxicFoodService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "독성 식품 등록 성공", result));
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ToxicFoodResponseDTO>> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody ToxicFoodRequestDTO dto) {
        ToxicFoodResponseDTO result = toxicFoodService.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "독성 식품 수정 성공", result));
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
        toxicFoodService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "독성 식품 삭제 성공", null));
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<ToxicFoodResponseDTO>>> getAll() {
        List<ToxicFoodResponseDTO> list = toxicFoodService.getAll();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "독성 식품 전체 조회 성공", list));
    }

    // 이름으로 조회
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<ToxicFoodResponseDTO>> getByName(@PathVariable("name") String name) {
        ToxicFoodResponseDTO result = toxicFoodService.getByName(name);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "독성 식품 이름 조회 성공", result));
    }

    // ID로 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ToxicFoodResponseDTO>> getById(@PathVariable("id") Long id) {
        ToxicFoodResponseDTO result = toxicFoodService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "독성 식품 ID 조회 성공", result));
    }
}
