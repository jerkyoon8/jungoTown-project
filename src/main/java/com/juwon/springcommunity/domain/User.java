package com.juwon.springcommunity.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
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
@Builder
public class User implements UserDetails {

    private Long id;

    private String username; // 임시 필드. 리팩토링 과정에서 제거 예정

    private String password;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하이어야 합니다.")
    private String nickname;

    private Role role; // 역할 필드 추가

    private String provider;
    private String providerId;

    @Override
    public String getUsername() {
        return this.email; // Spring Security에서 사용자의 고유 ID로 email을 사용하도록 설정
    }

    // 권한 확인 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority(this.role.getKey()));
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
