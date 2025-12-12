package com.mysite.test.member;

import lombok.Getter;

@Getter
public enum MemberRole {
   
    MEMBER("ROLE_MEMBER", "회원"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;

    MemberRole(String key, String title) {
        this.key = key;
        this.title = title;
    }
}