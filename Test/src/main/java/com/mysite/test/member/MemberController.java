package com.mysite.test.member;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mysite.test.pet.PetForm;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private static final String SIGNUP_FORM_SESSION_KEY = "signupPetForm";

    // STEP 1 : 회원 정보 입력
    @GetMapping("/signup")
    public String signupStep1(Model model, HttpSession session) {
        MemberSignupPetDto signupPetDto = (MemberSignupPetDto) session.getAttribute(SIGNUP_FORM_SESSION_KEY);
        if (signupPetDto == null) {
            signupPetDto = new MemberSignupPetDto();
            signupPetDto.setPets(new ArrayList<>());
            session.setAttribute(SIGNUP_FORM_SESSION_KEY, signupPetDto);
        }

        model.addAttribute("memberSignupPetDto", signupPetDto);
        return "member_signup_form";
    }

    @PostMapping("/signup")
    public String signupStep1Post(
            @Valid @ModelAttribute("memberSignupPetDto") MemberSignupPetDto memberSignupPetDto,
            BindingResult bindingResult,
            HttpSession session
    ) {
    	
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("loginId")
                    || bindingResult.hasFieldErrors("email")
                    || bindingResult.hasFieldErrors("password")) {
                return "member_signup_form";
            }
        }

        MemberSignupPetDto sessionDto = (MemberSignupPetDto) session.getAttribute(SIGNUP_FORM_SESSION_KEY);
        sessionDto.setLoginId(memberSignupPetDto.getLoginId());
        sessionDto.setPassword(memberSignupPetDto.getPassword());
        sessionDto.setEmail(memberSignupPetDto.getEmail());
        sessionDto.setStep1Completed(true);

        session.setAttribute(SIGNUP_FORM_SESSION_KEY, sessionDto);
        return "redirect:/member/signup/pet";
    }

    // STEP 2 : 반려동물 정보 입력
    @GetMapping("/signup/pet")
    public String signupStep2(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        MemberSignupPetDto sessionDto = (MemberSignupPetDto) session.getAttribute(SIGNUP_FORM_SESSION_KEY);

        // Step1을 완료하지 않았다면 리다이렉트
        if (sessionDto == null || !sessionDto.isStep1Completed()) {
            redirectAttributes.addFlashAttribute("error", "회원 정보를 먼저 입력해 주세요.");
            return "redirect:/member/signup";
        }

        // 반려동물 정보가 없으면 기본 1개 폼 추가
        if (sessionDto.getPets().isEmpty()) {
            sessionDto.getPets().add(new PetForm());
        }

        model.addAttribute("memberSignupPetDto", sessionDto);
        return "member_pet_form";
    }

    @PostMapping("/signup/pet")
    public String signupStep2Post(
            @Valid @ModelAttribute("memberSignupPetDto") MemberSignupPetDto memberSignupPetDto,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        MemberSignupPetDto finalDto = (MemberSignupPetDto) session.getAttribute(SIGNUP_FORM_SESSION_KEY);

        // 폼 에러 시 다시 반려동물 입력 페이지로 이동
        if (bindingResult.hasErrors()) {
            return "member_pet_form";
        }

        // 반려동물 이름이 비어 있으면 에러 처리
        boolean noValidPets = memberSignupPetDto.getPets() == null
                || memberSignupPetDto.getPets().isEmpty()
                || memberSignupPetDto.getPets().stream().allMatch(p -> p.getName().isBlank());

        if (noValidPets) {
            bindingResult.rejectValue("pets", "required", "반려동물 정보는 최소 1개 이상 필수입니다.");
            return "member_pet_form";
        }

        // 반려동물 정보 저장
        finalDto.setPets(memberSignupPetDto.getPets());

        try {
            memberService.createMemberAndPets(finalDto);
            session.removeAttribute(SIGNUP_FORM_SESSION_KEY);

            redirectAttributes.addFlashAttribute("success", "회원가입 및 반려동물 등록이 완료되었습니다! 로그인해 주세요.");
            return "redirect:/member/login";

        } catch (IllegalStateException e) {
            bindingResult.rejectValue("loginId", "duplicate", e.getMessage());
            return "member_pet_form";

        } catch (Exception e) {
            bindingResult.reject("signupFail", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return "member_pet_form";
        }
    }

    // 로그인
    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        if (logout != null) {
            model.addAttribute("logoutMsg", "성공적으로 로그아웃되었습니다.");
        }
        return "member_login_form";
    }
}
