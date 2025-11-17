package com.spring.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.spring.common.ApiResponse;
import com.spring.domain.PlaceType;
import com.spring.dto.PetPlaceRequestDTO;
import com.spring.dto.PetPlaceResponseDTO;
import com.spring.service.PetPlaceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/local/places")
@RequiredArgsConstructor
public class PetPlaceController {

    private final PetPlaceService service;

    // 장소 등록
    @PostMapping
    public ResponseEntity<ApiResponse<PetPlaceResponseDTO>> create(@Valid @RequestBody PetPlaceRequestDTO dto) {
        PetPlaceResponseDTO result = service.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "장소 등록 성공", result));
    }

    // 장소 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PetPlaceResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody PetPlaceRequestDTO dto) {

        PetPlaceResponseDTO result = service.update(id, dto);
        return ResponseEntity
                .ok(ApiResponse.success(HttpStatus.OK, "장소 수정 성공", result));
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "장소 삭제 성공", null));
    }

    // 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PetPlaceResponseDTO>> getById(@PathVariable("id") Long id) {
        PetPlaceResponseDTO result = service.getById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "단일 장소 조회 성공", result));
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<PetPlaceResponseDTO>>> getAll() {
        List<PetPlaceResponseDTO> list = service.getAll();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "전체 장소 조회 성공", list));
    }

    // 유형별 조회
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<PetPlaceResponseDTO>>> getByType(@PathVariable("type") PlaceType type) {
        List<PetPlaceResponseDTO> list = service.getByType(type);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "유형별 장소 조회 성공", list));
    }

    // 반려동물 동반 가능 장소만
    @GetMapping("/pet-friendly")
    public ResponseEntity<ApiResponse<List<PetPlaceResponseDTO>>> getPetFriendly() {
        List<PetPlaceResponseDTO> list = service.getPetFriendly();
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "반려동물 동반 가능 장소 조회 성공", list));
    }
}
