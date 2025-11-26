package com.mysite.test.toxicfood;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.test.exception.BadRequestException;
import com.mysite.test.exception.InternalServerException;
import com.mysite.test.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ToxicFoodService {

    private final ToxicFoodRepository toxicFoodRepository;

    // 독성 음식 등록
    public ToxicFoodResponseDTO create(ToxicFoodRequestDTO dto) {
        // 이름 중복 체크
        if (toxicFoodRepository.existsByName(dto.getName())) {
            throw new BadRequestException("이미 등록된 식품 이름입니다.");
        }

        ToxicFood food = new ToxicFood();
        food.setName(dto.getName());
        food.setCategory(dto.getCategory());
        food.setToxicityLevel(dto.getToxicityLevel());
        food.setDescription(dto.getDescription());
        food.setNote(dto.getNote());

        ToxicFood saved = toxicFoodRepository.save(food);
        return ToxicFoodResponseDTO.fromEntity(saved);
    }

    // 독성 음식 수정
    public ToxicFoodResponseDTO update(Long id, ToxicFoodRequestDTO dto) {
        ToxicFood existing = toxicFoodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("수정하려는 식품 정보가 존재하지 않습니다."));

        // @Valid 를 통과한 dto 라고 가정하고 전체 필드 덮어쓰기
        existing.setName(dto.getName());
        existing.setCategory(dto.getCategory());
        existing.setToxicityLevel(dto.getToxicityLevel());
        existing.setDescription(dto.getDescription());
        existing.setNote(dto.getNote());

        // existing 은 영속 상태라 save 없어도 되지만, 명시적으로 호출하고 싶으면 유지해도 됨
        ToxicFood updated = toxicFoodRepository.save(existing);
        return ToxicFoodResponseDTO.fromEntity(updated);
    }

    // 독성 음식 삭제
    public void delete(Long id) {
        if (!toxicFoodRepository.existsById(id)) {
            throw new NotFoundException("삭제하려는 식품 정보가 존재하지 않습니다.");
        }
        toxicFoodRepository.deleteById(id);
    }

    // 전체 목록 조회
    @Transactional(readOnly = true)
    public List<ToxicFoodResponseDTO> getAll() {
        List<ToxicFood> foods = toxicFoodRepository.findAll();

        if (foods.isEmpty()) {
            throw new NotFoundException("등록된 독성 식품이 없습니다.");
        }

        return foods.stream()
                .map(ToxicFoodResponseDTO::fromEntity)
                .toList();
    }

    // ID로 조회
    @Transactional(readOnly = true)
    public ToxicFoodResponseDTO getById(Long id) {
        ToxicFood food = toxicFoodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 ID의 식품 정보가 존재하지 않습니다."));
        return ToxicFoodResponseDTO.fromEntity(food);
    }

    // 이름으로 조회
    @Transactional(readOnly = true)
    public ToxicFoodResponseDTO getByName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("식품 이름을 입력해야 합니다.");
        }

        ToxicFood food = toxicFoodRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("해당 이름의 식품 정보를 찾을 수 없습니다."));

        return ToxicFoodResponseDTO.fromEntity(food);
    }
}
