package com.juwon.springcommunity.security.oauth;

import com.juwon.springcommunity.domain.Role;
import com.juwon.springcommunity.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    // registrationId에 따라 다른 메소드를 호출하여 OAuthAttributes 객체를 생성
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        // 현재는 google만 지원
        if ("google".equals(registrationId)) {
            return ofGoogle(userNameAttributeName, attributes);
        }
        // 나중에 Naver, Kakao 등을 추가할 수 있음
        // ex) if("naver".equals(registrationId)) { return ofNaver("response", attributes); }

        return null;
    }

    // Google 사용자 정보 파싱
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // User 엔티티 생성
    public User toEntity() {
        return User.builder()
                .username(name) // 소셜 로그인 사용자는 보통 이름을 username으로 사용
                .email(email)
                .password(UUID.randomUUID().toString()) // 임의의 비밀번호 설정
                .nickname(name) // 닉네임도 일단 이름으로 설정
                .role(Role.USER) // 기본 권한은 USER
                .provider("google")
                .providerId((String) attributes.get("sub")) // Google의 고유 식별자
                .build();
    }
}
