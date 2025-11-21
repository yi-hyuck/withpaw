package com.mysite.test.member;

import java.io.Serializable;
import java.util.List;

import com.mysite.test.pet.PetForm;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignupPetDto implements Serializable {

    @Size(min = 3, max = 25, message = "사용자 ID는 3~25자 사이여야 합니다.")
    @NotBlank(message = "사용자ID는 필수항목입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수항목입니다.")
    private String password;
    
    @NotBlank(message = "이메일은 필수항목입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email; 
    
   
    @Valid 
    private List<PetForm> pets; 
    private boolean step1Completed = false;
	
}
