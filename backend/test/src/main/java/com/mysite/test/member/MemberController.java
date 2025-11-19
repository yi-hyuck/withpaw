package com.mysite.test.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mysite.test.pet.Pet;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
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
    
    /**
     * 회원가입 및 반려동물 정보 등록 (단일 API 엔드포인트)
     * React Native 클라이언트가 전체 DTO를 한 번에 전송한다고 가정합니다.
     *
     * @param memberSignupPetDto 회원 정보 및 반려동물 정보를 포함하는 DTO
     * @param bindingResult 유효성 검사 결과
     * @return 성공 또는 실패 메시지를 담은 JSON 응답
     */
    
    @PostMapping("/signup")
    public ResponseEntity<?> signupStep1Post(
            @Valid @RequestBody MemberSignupPetDto memberSignupPetDto,
            BindingResult bindingResult,
            HttpSession session
    ) {
    	
        if (bindingResult.hasErrors()) {
        	if (bindingResult.hasFieldErrors("loginId")
                    || bindingResult.hasFieldErrors("email")
                    || bindingResult.hasFieldErrors("password")) {
                return new ResponseEntity<>(getValidationErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }
        }

        MemberSignupPetDto sessionDto = new MemberSignupPetDto();
        sessionDto.setLoginId(memberSignupPetDto.getLoginId());
        sessionDto.setPassword(memberSignupPetDto.getPassword());
        sessionDto.setEmail(memberSignupPetDto.getEmail());
        sessionDto.setStep1Completed(true);

        session.setAttribute(SIGNUP_FORM_SESSION_KEY, sessionDto);
        
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
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        MemberSignupPetDto finalDto = memberSignupPetDto;
        finalDto.setPets(memberSignupPetDto.getPets());
        
        if(bindingResult.hasErrors()) {
        	return new ResponseEntity<>(getValidationErrors(bindingResult), HttpStatus.BAD_REQUEST);
        }
        
        // 반려동물 이름이 비어 있으면 에러 처리
        boolean noValidPets = memberSignupPetDto.getPets() == null
                || memberSignupPetDto.getPets().isEmpty()
                || memberSignupPetDto.getPets().stream().allMatch(p -> p.getName().isBlank());

        if (noValidPets) {
        	Map<String, String> errors = new HashMap<>();
            errors.put("pets", "반려동물 정보는 최소 1개 이상 필수입니다.");
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            memberService.createMemberAndPets(finalDto);

            Map<String, String> response = new HashMap<>();
            response.put("success", "회원가입 및 반려동물 등록이 완료되었습니다! 로그인해 주세요.");
            return new ResponseEntity<>(response, HttpStatus.OK);

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

    // 로그인
//    @GetMapping("/login")
//    public String login(
//            @RequestParam(value = "error", required = false) String error,
//            @RequestParam(value = "logout", required = false) String logout,
//            Model model
//    ) {
//        if (error != null) {
//            model.addAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
//        }
//        if (logout != null) {
//            model.addAttribute("logoutMsg", "성공적으로 로그아웃되었습니다.");
//        }
//        return "member_login_form";
//    }
//    
}
