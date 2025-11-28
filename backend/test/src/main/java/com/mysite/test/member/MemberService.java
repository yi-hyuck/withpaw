package com.mysite.test.member;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
//        member.setRole(MemberRole.MEMBER);
        this.memberRepository.save(member);
        
        if (dto.getPets() != null && !dto.getPets().isEmpty()) {
            for (PetForm petForm : dto.getPets()) {
                petService.create(
                    member, 
                    petForm.getName(),
                    petForm.getBreed(),
                    petForm.getGender(),
                    petForm.getBirthDate(),
                    petForm.getNeuter(),
                    petForm.getWeight()
                );
            }
        }
    }
    
    public Member getMember(String loginId) {
        return memberRepository.findByLoginId(loginId)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + loginId));
    }
    
 // 이메일 수정
    @Transactional
    public void updateEmail(Member member, String newEmail) {
        // 이미 존재하는 이메일인지 확인 (단, 본인의 이메일은 허용)
        Optional<Member> existingMember = memberRepository.findByEmail(newEmail);
        
        if (existingMember.isPresent() && !existingMember.get().getLoginId().equals(member.getLoginId())) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }
        member.setEmail(newEmail);
        this.memberRepository.save(member);
    }

    // 비밀번호 변경
    @Transactional
    public void updatePassword(Member member, String newPassword) {
        // 새 비밀번호를 암호화하여 저장
        member.setPassword(passwordEncoder.encode(newPassword));
        this.memberRepository.save(member);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteMember(Member member) {
        // Member 삭제 시 연결된 Pet 데이터도 함께 삭제
        this.memberRepository.delete(member);
    }
    

}
