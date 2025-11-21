package com.mysite.test.pet;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysite.test.DataNotFoundException;
import com.mysite.test.member.Member;
import com.mysite.test.member.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pet")
public class PetController {

    private final PetService petService;
    private final MemberService memberService;

    
    // 반려동물 목록 조회 및 관리 페이지
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/manage")
    public String managePets(Principal principal, Model model) {
        Member owner = memberService.getMember(principal.getName());
        List<Pet> petList = petService.getPetsByMember(owner);
        
        model.addAttribute("petList", petList);
        return "pet_manage";
    }


    // 반려동물 추가 폼
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String createPet(PetForm petForm) {
        return "pet_form"; 
    }


    // 반려동물 추가 처리
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String createPet(@Valid PetForm petForm, BindingResult bindingResult, 
                            Principal principal) {
        if (bindingResult.hasErrors()) {
            return "pet_form";
        }
        
        Member owner = memberService.getMember(principal.getName());

        petService.create(
            owner, 
            petForm.getName(),
            petForm.getBreed(),
            petForm.getGender(),
            petForm.getBirthDate(),
            petForm.getNeuter(),
            petForm.getWeight()
        );
        
        return "redirect:/pet/manage";
    }


    // 반려동물 수정 폼
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{petId}")
    // DTO를 PetForm으로 통일
    public String modifyPet(PetForm petForm, @PathVariable("petId") Integer petId, 
                            Principal principal) {
        Pet pet = this.petService.getPet(petId);

        if (!pet.getOwner().getLoginId().equals(principal.getName())) {
            throw new DataNotFoundException("수정 권한이 없습니다.");
        }

        // 폼에 현재 펫 정보 채우기
        petForm.setPetId(pet.getPetId()); 
        petForm.setName(pet.getPetname());
        petForm.setBreed(pet.getBreed().getBreedname()); 
        petForm.setGender(pet.getGender());
        petForm.setBirthDate(pet.getBirthdate());
        petForm.setNeuter(pet.getNeuter());
        petForm.setWeight(pet.getWeight());
        
        return "pet_form";
    }


    // 반려동물 수정 처리 (품종/성별 제외)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{petId}")
    public String modifyPet(@Valid PetForm petForm, BindingResult bindingResult, 
                            Principal principal, @PathVariable("petId") Integer petId) {
        if (bindingResult.hasErrors()) {
            petForm.setPetId(petId);
            return "pet_form";
        }

        Pet pet = this.petService.getPet(petId);

        if (!pet.getOwner().getLoginId().equals(principal.getName())) {
            throw new DataNotFoundException("수정 권한이 없습니다.");
        }

        this.petService.update(
            pet, 
            petForm.getName(),
            petForm.getBirthDate(),
            petForm.getNeuter(),
            petForm.getWeight()
        );
        
        return "redirect:/pet/manage";
    }


    // 반려동물 삭제 처리
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{petId}")
    public String deletePet(@PathVariable("petId") Integer petId, Principal principal) {
        Pet pet = this.petService.getPet(petId);

        if (!pet.getOwner().getLoginId().equals(principal.getName())) {
            throw new DataNotFoundException("삭제 권한이 없습니다.");
        }
        
        this.petService.delete(pet);
        
        return "redirect:/pet/manage";
    }
}