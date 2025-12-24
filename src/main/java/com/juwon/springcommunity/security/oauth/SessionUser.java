package com.juwon.springcommunity.security.oauth;

import com.juwon.springcommunity.domain.Role;
import com.juwon.springcommunity.domain.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String nickname;
    private Role role;

    public SessionUser(User user) {
        this.id = user.getId();
        this.name = user.getUsername();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.role = user.getRole();
    }
}
