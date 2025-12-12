package com.mysite.test.disease;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mysite.test.place.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/diseases")
@RequiredArgsConstructor
public class DiseaseController {

	private final DiseaseService diseaseService;

	@PostMapping
	public ResponseEntity<ApiResponse<Disease>> create(@RequestBody Disease disease) {
		Disease saved = diseaseService.create(disease);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(HttpStatus.CREATED, "질병 등록 성공", saved));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<Disease>> update(@PathVariable("id") Long id, @RequestBody Disease disease) {
		Disease updated = diseaseService.update(id, disease);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "질병 수정 성공", updated));
	}

	// 전체조회
	@GetMapping
	public ResponseEntity<ApiResponse<List<Disease>>> getAll() {
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "질병 목록 조회 성공", diseaseService.getAll()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Disease>> getById(@PathVariable("id") Long id) {
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "질병 조회 성공", diseaseService.getById(id)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
		diseaseService.delete(id);
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "질병 삭제 성공", null));
	}
}
