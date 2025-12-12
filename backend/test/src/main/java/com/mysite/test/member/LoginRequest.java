package com.mysite.test.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    // RN에서 보내는 필드명과 일치해야 합니다. (RN: loginId, password)
    private String loginId; 
    private String password;
}