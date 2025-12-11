package com.juwon.springcommunity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequestDto {

    @NotBlank(message = "사용자 ID는 필수입니다.")
    @Size(min = 3, max = 50, message = "사용자 ID는 3자 이상 50자 이하이어야 합니다.")
    private String username;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;
    
    @NotBlank(message = "사용자 닉네임은 필수입니다.")
    @Size(min = 2, max = 10, message = "사용자 닉네임은 2자 이상 10자 이하여야 합니다.")
    private String nickname;
}
