package com.mysite.test.symptom;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.test.ApiResponse;
import com.mysite.test.member.Member;
import com.mysite.test.member.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/symptoms")
@RequiredArgsConstructor
public class SymptomController {

    private final SymptomService symptomService;
    private final MemberService memberService; // Principal에서 얻은 ID로 회원 정보를 조회하기 위해 추가

    // 증상 기록 등록 (POST /symptoms)
    @PreAuthorize("isAuthenticated()") // 로그인된 사용자만 접근 허용
    @PostMapping
    public ResponseEntity<ApiResponse<SymptomResponseDTO>> create(@Valid @RequestBody SymptomRequestDTO dto, Principal principal) {
        Member member = memberService.getMember(principal.getName()); // Principal 객체를 사용하여 현재 로그인한 회원 정보를 가져옴
        dto.setMemberId(member.getUserId().longValue()); // 클라이언트가 보낸 값은 무시하고, 서버가 인증한 사용자 ID만 사용
        
        SymptomResponseDTO result = symptomService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "증상 기록 등록 성공", result));
    }

    // 현재 로그인 회원의 모든 증상 기록 조회 (GET /symptoms/my)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my") 
    public ResponseEntity<ApiResponse<List<SymptomResponseDTO>>> getMySymptoms(Principal principal) {
    	// Principal에서 현재 로그인한 회원 ID를 추출
        Member member = memberService.getMember(principal.getName());
        Long memberId = member.getUserId().longValue(); 
        List<SymptomResponseDTO> list = symptomService.getByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "회원 전체 증상 기록 조회 성공", list));
    }

    // 단일 조회 (GET /symptoms/{id}) - 권한 검증 추가
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SymptomResponseDTO>> getById(@PathVariable("id") Long id, Principal principal) {
        Member member = memberService.getMember(principal.getName());
        Long currentMemberId = member.getUserId().longValue();
        // 서비스에서 조회와 동시에 해당 기록의 소유자가 현재 사용자인지 확인
        SymptomResponseDTO result = symptomService.getByIdAndCheckAuthority(id, currentMemberId);
        
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "단일 증상 기록 조회 성공", result));
    }

    // 수정 (PUT /symptoms/{id}) - 권한 검증 추가 및 DTO에 ID 설정
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SymptomResponseDTO>> update(@PathVariable("id") Long id,
                                                                  @Valid @RequestBody SymptomRequestDTO dto, Principal principal) {
        Member member = memberService.getMember(principal.getName());
        Long currentMemberId = member.getUserId().longValue();
        
        dto.setMemberId(currentMemberId); 
        // 로그인한 사용자 ID로 설정
        SymptomResponseDTO result = symptomService.update(id, dto, currentMemberId);
        // 서비스에서 권한 검증
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "증상 기록 수정 성공", result));
    }

    // 삭제 (DELETE /symptoms/{id}) - 로그인 사용자 권한 확인 후 삭제
    @PreAuthorize("isAuthenticated()") // 로그인된 사용자만 접근 허용
    @DeleteMapping("/{id}")
    // 반환 타입을 ResponseEntity<Void>로 변경 (DELETE 성공 시 204 응답) - 클라이언트 측 오류 방지
    public ResponseEntity<Void> delete(@PathVariable("id") Long id, Principal principal) {
        Member member = memberService.getMember(principal.getName());
        Long currentMemberId = member.getUserId().longValue(); // 현재 사용자 ID 추출
        
        // 서비스에 현재 사용자 ID를 전달하여 삭제 권한 검증을 위임
        symptomService.delete(id, currentMemberId);
        
        // DELETE 성공 시 204 응답 반환
        return ResponseEntity.noContent().build(); 
    }
}