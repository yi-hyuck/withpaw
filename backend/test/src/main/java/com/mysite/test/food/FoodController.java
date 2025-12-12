package com.mysite.test.food;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.test.place.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FoodController {

    private final FoodService foodService;
    
    // 음식 전체 조회
    @GetMapping
    public List<FoodResponseDto> getAllFoods() {
        return foodService.getAllFoods();
    }
    
    
    // id로 음식 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<FoodResponseDto> getFoodDetail(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(foodService.getFoodDetail(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}