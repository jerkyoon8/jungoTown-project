package com.juwon.springcommunity.config;

import com.juwon.springcommunity.domain.User;
import com.juwon.springcommunity.repository.UserRepository;
import com.juwon.springcommunity.security.oauth.SessionUser;
import com.juwon.springcommunity.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserRepository userRepository; // UserRepository 주입 추가

    @Bean
    public PasswordEncoder passwordEncoder() {
         return new BCryptPasswordEncoder();
    }
    
    // 일반 로그인 성공 시 세션 처리를 위한 핸들러 정의
    @Bean
    public AuthenticationSuccessHandler customLoginSuccessHandler() {
        return (request, response, authentication) -> {
            // 인증된 사용자 정보 가져오기
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            // DB에서 전체 사용자 정보 조회
            User user = userRepository.findByEmail(username) // 이메일을 username으로 사용한다고 가정
                    .orElseThrow(() -> new IllegalStateException("Cannot find user with email: " + username));

            // 세션에 SessionUser 저장
            HttpSession session = request.getSession();
            session.setAttribute("user", new SessionUser(user));

            // 홈페이지로 리디렉션
            response.sendRedirect("/");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/css/**", "/image/**", "/favicon.ico").permitAll()
                        .requestMatchers("/products/new", "/products/*/edit", "/products/*/delete", "/chat/**").authenticated() // 로그인 필요
                        .requestMatchers("/users/new").anonymous() //  비로그인시만
                        .requestMatchers("/users/updateUserForm").authenticated() // 로그인 필요
                        .requestMatchers("/users/userList").hasRole("ADMIN") // Admin 권한
                        .anyRequest().permitAll() // 그외 모두 접근 권한
                )
                .formLogin((form) -> form
                        .loginPage("/users/login") // 로그인 페이지
                        .loginProcessingUrl("/users/login") // 로그인 처리 url
                        .usernameParameter("email") // 이메일을 사용자 이름으로 사용
                        .successHandler(customLoginSuccessHandler()) // 직접 만든 핸들러 사용
                        .failureUrl("/users/login?error=true") // 실패시
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/users/login")
                        .successHandler(customLoginSuccessHandler())
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                )
                .logout((logout) -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }
}
