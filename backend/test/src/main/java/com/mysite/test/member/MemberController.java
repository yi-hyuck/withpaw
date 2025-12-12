package com.mysite.test.member;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysite.test.JwtTokenProvider;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private static final String SIGNUP_FORM_SESSION_KEY = "signupPetForm";

    // STEP 1 : 회원 정보 입력
//    @GetMapping("/signup")
//    public String signupStep1(Model model, HttpSession session) {
//        MemberSignupPetDto signupPetDto = (MemberSignupPetDto) session.getAttribute(SIGNUP_FORM_SESSION_KEY);
//        if (signupPetDto == null) {
//            signupPetDto = new MemberSignupPetDto();
//            signupPetDto.setPets(new ArrayList<>());
//            session.setAttribute(SIGNUP_FORM_SESSION_KEY, signupPetDto);
//        }
//
//        model.addAttribute("memberSignupPetDto", signupPetDto);
//        return "member_signup_form";
//    }

    private Map<String, String> getValidationErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        e -> e.getField(),
                        e -> e.getDefaultMessage()
                ));
    }
    
    @PostMapping("/signup")
    public ResponseEntity<?> signupStep1Post(
            @Valid @RequestBody MemberSignupPetDto memberSignupPetDto,
            BindingResult bindingResult
//            HttpSession session
    ) {
    	
        if (bindingResult.hasErrors()) {
        	if (bindingResult.hasFieldErrors("loginId")
                    || bindingResult.hasFieldErrors("email")
                    || bindingResult.hasFieldErrors("password")) {
                return new ResponseEntity<>(getValidationErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }
        }

//        MemberSignupPetDto sessionDto = new MemberSignupPetDto();
//        sessionDto.setLoginId(memberSignupPetDto.getLoginId());
//        sessionDto.setPassword(memberSignupPetDto.getPassword());
//        sessionDto.setEmail(memberSignupPetDto.getEmail());
//        sessionDto.setStep1Completed(true);
//
//        session.setAttribute(SIGNUP_FORM_SESSION_KEY, sessionDto);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "회원 기본 정보가 성공적으로 검증되었습니다. 다음 단계(반려동물 정보 입력)로 진행하세요.");
        response.put("nextUrl", "/member/signup/pet");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // STEP 2 : 반려동물 정보 입력
//    @GetMapping("/signup/pet")
//    public String signupStep2(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
//        MemberSignupPetDto sessionDto = (MemberSignupPetDto) session.getAttribute(SIGNUP_FORM_SESSION_KEY);
//
//        // Step1을 완료하지 않았다면 리다이렉트
//        if (sessionDto == null || !sessionDto.isStep1Completed()) {
//            redirectAttributes.addFlashAttribute("error", "회원 정보를 먼저 입력해 주세요.");
//            return "redirect:/member/signup";
//        }
//
//        // 반려동물 정보가 없으면 기본 1개 폼 추가
//        if (sessionDto.getPets().isEmpty()) {
//            sessionDto.getPets().add(new PetForm());
//        }
//
//        model.addAttribute("memberSignupPetDto", sessionDto);
//        return "member_pet_form";
//    }

    @PostMapping("/signup/pet")
    public ResponseEntity<?> signupStep2Post(
            @Valid @RequestBody MemberSignupPetDto memberSignupPetDto,
            BindingResult bindingResult
//            HttpSession session,
//            RedirectAttributes redirectAttributes
    ) {
//        MemberSignupPetDto finalDto = memberSignupPetDto;
//        finalDto.setPets(memberSignupPetDto.getPets());
//        
        if(bindingResult.hasErrors()) {
        	return new ResponseEntity<>(getValidationErrors(bindingResult), HttpStatus.BAD_REQUEST);
        }
        
        // 반려동물 이름이 비어 있으면 에러 처리
        boolean noValidPets = memberSignupPetDto.getPets() == null
                || memberSignupPetDto.getPets().isEmpty()
                || memberSignupPetDto.getPets().stream().allMatch(p -> p.getPetname().isBlank());

        if (noValidPets) {
        	Map<String, String> errors = new HashMap<>();
            errors.put("pets", "반려동물 정보는 최소 1개 이상 필수입니다.");
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            memberService.createMemberAndPets(memberSignupPetDto);

            Map<String, String> response = new HashMap<>();
            response.put("success", "회원가입 및 반려동물 등록이 완료되었습니다! 로그인해 주세요.");
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalStateException e) {
        	Map<String, String> errors = new HashMap<>();
        	errors.put("loginId", e.getMessage());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
        	Map<String, String> response = new HashMap<>();
            response.put("error", "회원가입 중 서버 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        
        try {
            // 1. Spring Security의 AuthenticationManager를 사용하여 사용자 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getLoginId(),
                    loginRequest.getPassword()
                )
            );

            // 2. 인증에 성공하면 SecurityContext에 Authentication 객체를 저장합니다. (선택 사항이지만 토큰 인증 전에 필요)
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. 인증된 사용자 정보(Principal)를 기반으로 JWT 토큰 생성
            String token = jwtTokenProvider.createToken(authentication);

            // 4. 응답 DTO 생성 (토큰과 사용자 기본 정보를 포함)
            Member member = memberService.getMember(loginRequest.getLoginId());
            MemberResponseDto memberDto = MemberResponseDto.from(member);
            
            // 5. 클라이언트에게 토큰과 사용자 정보를 JSON 형태로 반환
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("memberInfo", memberDto);
            response.put("message", "로그인 성공");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            // 인증 실패 (BadCredentialsException 등) 시 401 Unauthorized 반환
            Map<String, String> errors = new HashMap<>();
            
            // 사용자에게 구체적인 예외 대신 일반적인 메시지를 제공하는 것이 보안상 안전합니다.
            errors.put("error", "아이디 또는 비밀번호가 올바르지 않습니다."); 
            
            // DEBUG 레벨에서 실제 예외를 로깅하는 것이 좋습니다.
            // log.warn("로그인 실패: {}", e.getMessage()); 
            
            return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
        }
    }
    
    @GetMapping("/info")
    public ResponseEntity<?> getMemberInfo() {
        // 1. Spring Security Context에서 현재 인증된 사용자 정보(Authentication) 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            // 인증되지 않은 사용자라면 401 Unauthorized 반환
            return new ResponseEntity<>("토큰이 유효하지 않거나 로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        // 2. 인증된 사용자 ID (LoginId) 가져오기
        String loginId = authentication.getName(); // UserDetails 객체의 username(loginId) 사용
        
        try {
            // 3. MemberService를 통해 상세 정보 (회원 + 반려동물) 조회
            Member member = memberService.getMember(loginId);
            
            // 4. 응답 DTO 생성 및 반환
            // MemberResponseDto는 회원 정보와 반려동물 정보를 담을 새로운 DTO입니다.
            return new ResponseEntity<>(MemberResponseDto.from(member), HttpStatus.OK);
            
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>("사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("정보 조회 중 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
// // 회원정보 관리 페이지
//    @GetMapping("/mypage")
//    public String showMypage(Principal principal, Model model, MemberUpdateForm memberUpdateForm) {
//        Member member = memberService.getMember(principal.getName());
//        
//        // 폼 객체에 현재 이메일 정보를 미리 설정
//        memberUpdateForm.setEmail(member.getEmail()); 
//        
//        model.addAttribute("member", member);
//        return "member_mypage"; 
//    }

    // 이메일 및 비밀번호 수정 처리
//    @PostMapping("/update")
//    public String updateMember(@Valid MemberUpdateForm memberUpdateForm, BindingResult bindingResult, 
//                                 Principal principal, Model model) {
//                                     
//        Member member = memberService.getMember(principal.getName());
//
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("member", member); // 오류 시 기존 정보 유지
//            return "member_mypage";
//        }
//        
//        try {
//            // 이메일 수정 로직
//            if (!member.getEmail().equals(memberUpdateForm.getEmail())) {
//                memberService.updateEmail(member, memberUpdateForm.getEmail());
//            }
//
//            // 비밀번호 변경 로직
//            if (memberUpdateForm.getNewPassword() != null && !memberUpdateForm.getNewPassword().isEmpty()) {
//                
//                // 현재 비밀번호 확인
//                if (!passwordEncoder.matches(memberUpdateForm.getCurrentPassword(), member.getPassword())) {
//                    bindingResult.rejectValue("currentPassword", "passwordInCorrect", "현재 비밀번호가 일치하지 않습니다.");
//                    model.addAttribute("member", member);
//                    return "member_mypage";
//                }
//                
//                // 새 비밀번호와 확인 비밀번호 일치 확인
//                if (!memberUpdateForm.getNewPassword().equals(memberUpdateForm.getNewPasswordConfirm())) {
//                    bindingResult.rejectValue("newPasswordConfirm", "passwordMismatch", "새 비밀번호 확인이 일치하지 않습니다.");
//                    model.addAttribute("member", member);
//                    return "member_mypage";
//                }
//
//                memberService.updatePassword(member, memberUpdateForm.getNewPassword());
//            }
//            
//            // 모든 수정 성공 시 마이페이지로 리다이렉트
//            return "redirect:/member/mypage?success=update"; 
//            
//        } catch (IllegalStateException e) {
//            bindingResult.rejectValue("email", "emailAlreadyInUse", e.getMessage());
//            model.addAttribute("member", member);
//            return "member_mypage";
//        }
//    }
    @PostMapping("/update")
    public ResponseEntity<?> updateMember(
            @Valid @RequestBody MemberUpdateForm memberUpdateForm,
            BindingResult bindingResult,
            Principal principal
    ) {
        String loginId = principal.getName(); 
        Member member = memberService.getMember(loginId);

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(getValidationErrors(bindingResult), HttpStatus.BAD_REQUEST);
        }
        
        try {
            if (!member.getEmail().equals(memberUpdateForm.getEmail())) {
                memberService.updateEmail(member, memberUpdateForm.getEmail());
            }

            // 비밀번호 변경
            if (memberUpdateForm.getNewPassword() != null && !memberUpdateForm.getNewPassword().isEmpty()) {
                
                if (!passwordEncoder.matches(memberUpdateForm.getCurrentPassword(), member.getPassword())) {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("currentPassword", "현재 비밀번호가 일치하지 않습니다.");
                    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
                }
                
                // 새 비밀번호와 확인 비밀번호 일치 확인
                if (!memberUpdateForm.getNewPassword().equals(memberUpdateForm.getNewPasswordConfirm())) {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("newPasswordConfirm", "새 비밀번호 확인이 일치하지 않습니다.");
                    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
                }

                memberService.updatePassword(member, memberUpdateForm.getNewPassword());
            }
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "회원 정보가 성공적으로 수정되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (IllegalStateException e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("email", e.getMessage());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
    }
    
    // 회원 탈퇴 처리
    @PostMapping("/delete")
    public ResponseEntity<?> deleteMember(Principal principal) {
    	String loginId = principal.getName();
        Member member = memberService.getMember(loginId);
        memberService.deleteMember(member);
        
        // 회원 탈퇴 후 로그아웃 처리 및 메인 페이지로 이동
        Map<String, String> response = new HashMap<>();
        response.put("message", "회원탈퇴 완료");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Principal principal){
    	SecurityContextHolder.clearContext();
    	Map<String, String> response = new HashMap<>();
    	response.put("message", "로그아웃 성공");
    	return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
}
