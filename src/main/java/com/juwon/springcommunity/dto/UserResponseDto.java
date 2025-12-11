package com.juwon.springcommunity.dto;

import com.juwon.springcommunity.domain.Role;
import com.juwon.springcommunity.domain.User;
import lombok.Data;


// 서비스 외의 계층에서 User 의 보안을 위해
// 비밀번호등 민감한 것들을 가리고 다른 계층에 제공하기 위해 사용한다.

@Data
public class UserResponseDto {

    private Long id;
    private String username;
    private String email;
    private String nickname;
    private Role role;

    // User 엔티티를 UserResponseDto로 변환하는 생성자
    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.role = user.getRole();
    }
}
