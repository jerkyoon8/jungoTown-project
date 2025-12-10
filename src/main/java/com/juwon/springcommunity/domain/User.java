package com.juwon.springcommunity.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    private Long id;

    @NotBlank(message = "사용자 이름은 필수입니다.")
    @Size(min = 3, max = 50, message = "사용자 이름은 3자 이상 50자 이하이어야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하이어야 합니다.")
    private String nickname;

    private Role role; // 역할 필드 추가

    // 권한 확인 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority(this.role.name()));
    }

    // 계정 만료 여부 확인
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 여부 확인
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 자격 증명 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화 상태 여부
    @Override
    public boolean isEnabled() {
        return true;
    }
}
