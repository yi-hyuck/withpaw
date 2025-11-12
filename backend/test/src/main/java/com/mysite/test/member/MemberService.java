package com.mysite.test.member;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.test.pet.PetForm;
import com.mysite.test.pet.PetService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final PetService petService; 
    
    @Transactional
    public void createMemberAndPets(MemberSignupPetDto dto) throws Exception {
      
        if (memberRepository.findByLoginId(dto.getLoginId()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 사용자 ID입니다.");
        }
        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
        
        Member member = new Member(); 
        member.setLoginId(dto.getLoginId());
        member.setPassword(passwordEncoder.encode(dto.getPassword())); 
        member.setEmail(dto.getEmail());
        member.setRole(MemberRole.MEMBER);
        this.memberRepository.save(member);
        
        if (dto.getPets() != null && !dto.getPets().isEmpty()) {
            for (PetForm petForm : dto.getPets()) {
                petService.create(
                    member, 
                    petForm.getName(),
                    petForm.getSpecies(),
                    petForm.getGender(),
                    petForm.getBirthDate(),
                    petForm.getNeuter(),
                    petForm.getWeight()
                );
            }
        }
    }

}
