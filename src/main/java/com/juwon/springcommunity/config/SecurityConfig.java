package com.juwon.springcommunity.config;

import com.juwon.springcommunity.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
         return new BCryptPasswordEncoder();
    }

    
        @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/products/new", "/products/*/edit", "/products/*/delete", "/chat/**").authenticated() // 로그인 필요
                        .requestMatchers("/users/new").anonymous() //  비로그인시만
                        .requestMatchers("/users/updateUserForm").authenticated() // 로그인 필요
                        .requestMatchers("/users/userList").hasRole("ADMIN") // Admin 권한
                        .anyRequest().permitAll() // 그외 모두 접근 권한
                )
                .formLogin((form) -> form
                        .loginPage("/users/login") // 로그인 페이지
                        .loginProcessingUrl("/users/login") // 로그인 처리 url
                        .defaultSuccessUrl("/") // 성공시
                        .failureUrl("/users/login?error=true") // 실패시
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/users/login")
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
