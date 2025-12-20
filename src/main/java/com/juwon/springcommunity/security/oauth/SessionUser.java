package com.juwon.springcommunity.security.oauth;

import com.juwon.springcommunity.domain.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;

    public SessionUser(User user) {
        this.name = user.getUsername();
        this.email = user.getEmail();
    }
}
