package com.juwon.springcommunity.service;

import com.juwon.springcommunity.domain.User;
import com.juwon.springcommunity.repository.UserRepository;
import com.juwon.springcommunity.security.oauth.OAuthAttributes;
import com.juwon.springcommunity.security.oauth.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 현재 로그인 진행 중인 서비스를 구분하는 코드 (google, naver, kakao...)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // OAuth2 로그인 진행 시 키가 되는 필드값. Primary Key와 같은 의미.
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        // 세션에 사용자 정보를 저장하기 위한 DTO 클래스
        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .orElseGet(() -> {
                    User newUser = attributes.toEntity();
                    // 닉네임 중복 체크 및 유니크한 닉네임 생성
                    String nickname = newUser.getNickname();
                    if (userRepository.existsByNickname(nickname)) {
                        nickname = generateUniqueNickname(nickname);
                        newUser.setNickname(nickname);
                    }
                    return newUser;
                });

        // Mybatis는 save 메소드가 따로 없으며, insert/update 쿼리에서 id가 없으면 insert, 있으면 update를 수행.
        if (user.getId() == null) {
            userRepository.save(user); // 신규 사용자 저장
        }
        
        return user;
    }

    private String generateUniqueNickname(String baseNickname) {
        String uniqueNickname = baseNickname;
        int suffix = 1;
        while (userRepository.existsByNickname(uniqueNickname)) {
            uniqueNickname = baseNickname + suffix++;
            // 무한 루프 방지 및 길이 제한 고려 (필요시)
            if (suffix > 999) {
                uniqueNickname = baseNickname + System.currentTimeMillis() % 1000;
                break;
            }
        }
        return uniqueNickname;
    }
}
