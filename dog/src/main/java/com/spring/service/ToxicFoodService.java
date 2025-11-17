package com.spring.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.domain.ToxicFood;
import com.spring.dto.ToxicFoodRequestDTO;
import com.spring.dto.ToxicFoodResponseDTO;
import com.spring.exception.BadRequestException;
import com.spring.exception.InternalServerException;
import com.spring.exception.NotFoundException;
import com.spring.repository.ToxicFoodRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ToxicFoodService {

    private final ToxicFoodRepository toxicFoodRepository;

    // 독성 음식 등록
    public ToxicFoodResponseDTO create(ToxicFoodRequestDTO dto) {
        if (toxicFoodRepository.existsByName(dto.getName()))
            throw new BadRequestException("이미 등록된 식품 이름입니다.");

        try {
            ToxicFood food = new ToxicFood();
            food.setName(dto.getName());
            food.setCategory(dto.getCategory());
            food.setToxicityLevel(dto.getToxicityLevel());
            food.setDescription(dto.getDescription());
            food.setNote(dto.getNote());

            ToxicFood saved = toxicFoodRepository.save(food);
            return ToxicFoodResponseDTO.fromEntity(saved);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("독성 식품 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 독성 음식 수정
    public ToxicFoodResponseDTO update(Long id, ToxicFoodRequestDTO dto) {
        ToxicFood existing = toxicFoodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("수정하려는 식품 정보가 존재하지 않습니다."));

        try {
            if (dto.getName() != null)
                existing.setName(dto.getName());
            if (dto.getCategory() != null)
                existing.setCategory(dto.getCategory());
            if (dto.getToxicityLevel() != null)
                existing.setToxicityLevel(dto.getToxicityLevel());
            if (dto.getDescription() != null)
                existing.setDescription(dto.getDescription());
            if (dto.getNote() != null)
                existing.setNote(dto.getNote());

            ToxicFood updated = toxicFoodRepository.save(existing);
            return ToxicFoodResponseDTO.fromEntity(updated);

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("식품 정보 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 독성 음식 삭제
    public void delete(Long id) {
        if (!toxicFoodRepository.existsById(id))
            throw new NotFoundException("삭제하려는 식품 정보가 존재하지 않습니다.");
        try {
            toxicFoodRepository.deleteById(id);
        } catch (Exception e) {
            throw new InternalServerException("식품 정보 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 전체 목록 조회
    @Transactional(readOnly = true)
    public List<ToxicFoodResponseDTO> getAll() {
        try {
            List<ToxicFood> foods = toxicFoodRepository.findAll();
            if (foods.isEmpty())
                throw new NotFoundException("등록된 독성 식품이 없습니다.");

            return foods.stream()
                    .map(ToxicFoodResponseDTO::fromEntity)
                    .toList();

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("식품 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ID로 조회
    @Transactional(readOnly = true)
    public ToxicFoodResponseDTO getById(Long id) {
        try {
            ToxicFood food = toxicFoodRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("해당 ID의 식품 정보가 존재하지 않습니다."));
            return ToxicFoodResponseDTO.fromEntity(food);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("식품 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 이름으로 조회
    @Transactional(readOnly = true)
    public ToxicFoodResponseDTO getByName(String name) {
        if (name == null || name.isBlank())
            throw new BadRequestException("식품 이름을 입력해야 합니다.");

        try {
            ToxicFood food = toxicFoodRepository.findByName(name)
                    .orElseThrow(() -> new NotFoundException("해당 이름의 식품 정보를 찾을 수 없습니다."));
            return ToxicFoodResponseDTO.fromEntity(food);
        } catch (NotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("식품 이름 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
