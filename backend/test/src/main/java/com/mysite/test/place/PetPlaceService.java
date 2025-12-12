package com.mysite.test.place;

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
public class PetPlaceService {

    private final PetPlaceRepository repository;

    // 장소 등록
    public PetPlaceResponseDTO create(PetPlaceRequestDTO dto) {
        if (dto == null)
            throw new BadRequestException("요청 데이터가 비어 있습니다.");
        if (dto.getLatitude() == null || dto.getLongitude() == null)
            throw new BadRequestException("위도와 경도는 필수입니다.");
        if (dto.getType() == null)
            throw new BadRequestException("장소 유형은 필수입니다.");

        try {
            PetPlace entity = dto.toEntity();
            PetPlace saved = repository.save(entity);
            return PetPlaceResponseDTO.fromEntity(saved);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("장소 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 장소 수정
    public PetPlaceResponseDTO update(Long id, PetPlaceRequestDTO dto) {
        if (id == null || id <= 0)
            throw new BadRequestException("유효하지 않은 장소 ID입니다.");

        PetPlace existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 장소를 찾을 수 없습니다."));

        try {
            if (dto.getName() != null)
                existing.setName(dto.getName());
            if (dto.getAddress() != null)
                existing.setAddress(dto.getAddress());
            if (dto.getPhone() != null)
                existing.setPhone(dto.getPhone());
            if (dto.getLatitude() != null)
                existing.setLatitude(dto.getLatitude());
            if (dto.getLongitude() != null)
                existing.setLongitude(dto.getLongitude());
            if (dto.getType() != null)
                existing.setType(dto.getType());
            if (dto.getDescription() != null)
                existing.setDescription(dto.getDescription());
            if (dto.getPetAllowed() != null)
                existing.setPetAllowed(dto.getPetAllowed());

            PetPlace updated = repository.save(existing);
            return PetPlaceResponseDTO.fromEntity(updated);
        } catch (Exception e) {
            throw new InternalServerException("장소 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 장소 삭제
    public void delete(Long id) {
        if (!repository.existsById(id))
            throw new NotFoundException("삭제할 장소가 존재하지 않습니다.");

        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new InternalServerException("장소 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 단일 조회
    public PetPlaceResponseDTO getById(Long id) {
        PetPlace place = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 장소를 찾을 수 없습니다."));
        return PetPlaceResponseDTO.fromEntity(place);
    }

    // 전체 조회
    public List<PetPlaceResponseDTO> getAll() {
        List<PetPlace> list = repository.findAll();
        if (list.isEmpty())
            throw new NotFoundException("등록된 장소가 없습니다.");
        return list.stream().map(PetPlaceResponseDTO::fromEntity).toList();
    }

    // 유형별 조회
    public List<PetPlaceResponseDTO> getByType(PlaceType type) {
        List<PetPlace> list = repository.findByType(type);
        if (list.isEmpty())
            throw new NotFoundException("해당 유형의 장소가 없습니다.");
        return list.stream().map(PetPlaceResponseDTO::fromEntity).toList();
    }

    // 반려동물 동반 가능 장소 조회
    public List<PetPlaceResponseDTO> getPetFriendly() {
        List<PetPlace> list = repository.findByPetAllowedTrue();
        if (list.isEmpty())
            throw new NotFoundException("반려동물 동반 가능한 장소가 없습니다.");
        return list.stream().map(PetPlaceResponseDTO::fromEntity).toList();
    }
}
