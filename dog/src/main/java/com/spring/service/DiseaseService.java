package com.spring.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.domain.Disease;
import com.spring.exception.BadRequestException;
import com.spring.exception.InternalServerException;
import com.spring.exception.NotFoundException;
import com.spring.repository.DiseaseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;

    @Transactional
    public Disease create(Disease disease) {
        if (disease.getName() == null || disease.getName().isBlank())
            throw new BadRequestException("질병 이름은 필수입니다.");

        try {
            return diseaseRepository.save(disease);
        } catch (Exception e) {
            throw new InternalServerException("질병 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Transactional
    public Disease update(Long id, Disease updatedDisease) {
        if (id == null || id <= 0)
            throw new BadRequestException("유효하지 않은 질병 ID입니다.");

        Disease existing = diseaseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 질병이 존재하지 않습니다."));

        if (updatedDisease.getName() != null)
            existing.setName(updatedDisease.getName());
        if (updatedDisease.getDescription() != null)
            existing.setDescription(updatedDisease.getDescription());
        if (updatedDisease.getRelatedSymptomIds() != null)
            existing.setRelatedSymptomIds(updatedDisease.getRelatedSymptomIds());

        try {
            return diseaseRepository.save(existing);
        } catch (Exception e) {
            throw new InternalServerException("질병 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public List<Disease> getAll() {
        return diseaseRepository.findAll();
    }

    public Disease getById(Long id) {
        return diseaseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 질병이 존재하지 않습니다."));
    }

    @Transactional
    public void delete(Long id) {
        Disease existing = diseaseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("삭제하려는 질병이 존재하지 않습니다."));
        diseaseRepository.delete(existing);
    }
}