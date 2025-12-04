package com.mysite.test.food;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodService {

    private final FoodRepository foodRepository;

    public List<FoodResponseDto> getAllFoods() {
    	
        return foodRepository.findAll()
        		.stream()
                .map(FoodResponseDto::new)
                .toList();
    }

    public FoodResponseDto getFoodDetail(Long id) {
    	
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 음식이 없습니다. ID: " + id));
        
        return new FoodResponseDto(food);
    }
}