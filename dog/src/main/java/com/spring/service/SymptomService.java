package com.spring.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.domain.Disease;
import com.spring.domain.Symptom;
import com.spring.dto.SymptomRequestDTO;
import com.spring.dto.SymptomResponseDTO;
import com.spring.exception.BadRequestException;
import com.spring.exception.InternalServerException;
import com.spring.exception.NotFoundException;
import com.spring.repository.DiseaseRepository;
import com.spring.repository.SymptomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SymptomService {

    private final SymptomRepository symptomRepository;
    private final DiseaseRepository diseaseRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 증상 기록 등록
    public SymptomResponseDTO create(SymptomRequestDTO dto) {

        if (dto.getSelectedSymptomIds() == null || dto.getSelectedSymptomIds().isEmpty())
            throw new BadRequestException("선택된 증상 목록이 비어 있습니다.");

        Symptom symptom = new Symptom();
        symptom.setMemberId(dto.getMemberId());
        symptom.setPetId(dto.getPetId());
        symptom.setSymptomDate(dto.getSymptomDate());
        symptom.setDescription(dto.getDescription());
        symptom.setCreatedAt(LocalDateTime.now());

        try {
            // List<Long> → JSON
            String selectedJson = objectMapper.writeValueAsString(dto.getSelectedSymptomIds());
            symptom.setSelectedSymptomIds(selectedJson);

            // 관련 질병 찾기
            List<Disease> suspected = dto.getSelectedSymptomIds().stream()
                    .flatMap(id -> diseaseRepository.findBySymptomIds(id).stream())
                    .distinct()
                    .toList();

            String suspectedJson = objectMapper.writeValueAsString(
                    suspected.stream().map(Disease::getId).toList()
            );
            symptom.setSuspectedDiseaseIds(suspectedJson);

            Symptom saved = symptomRepository.save(symptom);
            return SymptomResponseDTO.fromEntity(saved);

        } catch (Exception e) {
            throw new InternalServerException("증상 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    // 증상 기록 수정
    public SymptomResponseDTO update(Long id, SymptomRequestDTO dto) {

        Symptom existing = symptomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 증상 기록이 존재하지 않습니다."));

        try {
            if (dto.getDescription() != null && !dto.getDescription().isBlank())
                existing.setDescription(dto.getDescription());

            if (dto.getSymptomDate() != null)
                existing.setSymptomDate(dto.getSymptomDate());

            // 선택 증상 변경
            if (dto.getSelectedSymptomIds() != null && !dto.getSelectedSymptomIds().isEmpty()) {

                // List<Long> → JSON 문자열 저장
                existing.setSelectedSymptomIds(
                        objectMapper.writeValueAsString(dto.getSelectedSymptomIds())
                );

                List<Long> selectedIds = dto.getSelectedSymptomIds();

                // 관련 질병 추론
                List<Disease> suspected = selectedIds.stream()
                        .flatMap(id2 -> diseaseRepository.findBySymptomIds(id2).stream())
                        .distinct()
                        .toList();

                existing.setSuspectedDiseaseIds(
                        objectMapper.writeValueAsString(
                                suspected.stream().map(Disease::getId).toList()
                        )
                );
            }

            Symptom updated = symptomRepository.save(existing);
            return SymptomResponseDTO.fromEntity(updated);

        } catch (BadRequestException | NotFoundException e) {
            throw e;
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
