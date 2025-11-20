package com.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.common.ApiResponse;
import com.spring.service.PlaceService;

@RestController
@RequestMapping("/api/kakao/places")
public class PlaceController {

    private final PlaceService placeService;

    @Autowired
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> searchPlaces(
    		@RequestParam(name = "query") String query,
            @RequestParam(name = "lat") double lat,
            @RequestParam(name = "lng") double lng,
            @RequestParam(name = "radius", defaultValue = "2000") int radius
    ) {
    	
        int clamped = Math.max(1, Math.min(radius, 20_000));
        var places = placeService.searchPlaces(query, lat, lng, clamped);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "장소 검색 성공", places));
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, e.getMessage()));
    }
    
    

    
}
