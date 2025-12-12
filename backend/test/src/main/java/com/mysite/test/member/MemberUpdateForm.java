package com.mysite.test.member;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateForm {

    // 이메일 수정
    @Email(message = "유효하지 않은 이메일 형식입니다.")
    private String email;

    // 비밀번호 변경 시 사용
    private String currentPassword; // 현재 비밀번호 확인

    private String newPassword;     // 새 비밀번호

    private String newPasswordConfirm; // 새 비밀번호 확인 
}