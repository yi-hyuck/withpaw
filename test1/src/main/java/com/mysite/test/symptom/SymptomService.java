package com.mysite.test.symptom;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.test.exception.BadRequestException;
import com.mysite.test.exception.NotFoundException;

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
        Symptom saved = symptomRepository.save(symptom);
        return SymptomResponseDTO.fromEntity(saved);
    }

    // 증상 기록 수정
    public SymptomResponseDTO update(Long id, SymptomRequestDTO dto, Long memberId) {
        Symptom existing = symptomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 증상 기록이 존재하지 않습니다."));
        
        // 권한 검증 로직
        if (!existing.getMemberId().equals(memberId)) { 
            throw new AccessDeniedException("해당 기록을 수정할 권한이 없습니다.");
        }
        
        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setSymptomDate(dto.getSymptomDate());
        Symptom updated = symptomRepository.save(existing);
        return SymptomResponseDTO.fromEntity(updated);
    }

    // 증상 기록 삭제
    public void delete(Long id, Long memberId) {
        Symptom symptom = symptomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("삭제하려는 증상 기록이 존재하지 않습니다."));

        // 권한 검증 로직
        if (!symptom.getMemberId().equals(memberId)) { 
            throw new AccessDeniedException("해당 기록을 삭제할 권한이 없습니다.");
        }
        
        symptomRepository.delete(symptom);
    }

    // 단일 증상 기록 조회
    @Transactional(readOnly = true)
    public SymptomResponseDTO getByIdAndCheckAuthority(Long id, Long memberId) {
        Symptom symptom = symptomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 증상 기록이 존재하지 않습니다."));
        
        // 권한 검증 로직
        if (!symptom.getMemberId().equals(memberId)) {
            throw new AccessDeniedException("해당 기록을 조회할 권한이 없습니다.");
        }
        
        return SymptomResponseDTO.fromEntity(symptom);
    }
    
    // 특정 회원의 증상 기록 조회 (조회 오류 해결)
    @Transactional(readOnly = true)
    public List<SymptomResponseDTO> getByMember(Long memberId) {
        List<Symptom> symptoms = symptomRepository.findAllByMemberId(memberId);
        
        // 기록이 없더라도 예외로 처리하지 않고 빈 리스트로 반환
        return symptoms.stream()
                .map(SymptomResponseDTO::fromEntity)
                .toList();
    }
}