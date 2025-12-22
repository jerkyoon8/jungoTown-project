package com.juwon.springcommunity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequestDto {

    @NotBlank(message = "사용자 닉네임은 필수입니다.")
    @Size(min = 2, max = 10, message = "사용자 닉네임은 2자 이상 10자 이하여야 합니다.")
    private String nickname;
}
