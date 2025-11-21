package com.spring.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.domain.Symptom;
import com.spring.dto.SymptomRequestDTO;
import com.spring.dto.SymptomResponseDTO;
import com.spring.exception.BadRequestException;
import com.spring.exception.InternalServerException;
import com.spring.exception.NotFoundException;
import com.spring.repository.SymptomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SymptomService {

    private final SymptomRepository symptomRepository;
        
    // 증상 기록 등록
    public SymptomResponseDTO create(SymptomRequestDTO dto) {
        if (dto == null) throw new BadRequestException("요청 데이터가 비어 있습니다.");

        Symptom symptom = dto.toEntity();
        try {
            return SymptomResponseDTO.fromEntity(symptomRepository.save(symptom));
        } catch (Exception e) {
            throw new InternalServerException("증상 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    // 증상 기록 수정
    public SymptomResponseDTO update(Long id, SymptomRequestDTO dto) {
        Symptom existing = symptomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 증상 기록이 존재하지 않습니다."));

        try {
            if (dto.getTitle() != null && !dto.getTitle().isBlank())
                existing.setTitle(dto.getTitle());
            if (dto.getDescription() != null && !dto.getDescription().isBlank())
                existing.setDescription(dto.getDescription());

            return SymptomResponseDTO.fromEntity(symptomRepository.save(existing));
        } catch (Exception e) {
            throw new InternalServerException("증상 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 증상 기록 삭제
    public void delete(Long id) {
        if (!symptomRepository.existsById(id))
            throw new NotFoundException("삭제하려는 증상 기록이 존재하지 않습니다.");
        try {
            symptomRepository.deleteById(id);
        } catch (Exception e) {
            throw new InternalServerException("증상 기록 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 단일 증상 기록 조회
    @Transactional(readOnly = true)
    public SymptomResponseDTO getById(Long id) {
        try {
            Symptom symptom = symptomRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("해당 증상 기록이 존재하지 않습니다."));
            return SymptomResponseDTO.fromEntity(symptom);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("증상 기록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 회원의 증상 기록 조회
    @Transactional(readOnly = true)
    public List<SymptomResponseDTO> getByMember(Long memberId) {
        try {
            List<Symptom> symptoms = symptomRepository.findAllByMemberId(memberId);
            if (symptoms.isEmpty())
                throw new NotFoundException("해당 회원의 증상 기록이 존재하지 않습니다.");
            return symptoms.stream()
                    .map(SymptomResponseDTO::fromEntity)
                    .toList();
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("회원 증상 기록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
