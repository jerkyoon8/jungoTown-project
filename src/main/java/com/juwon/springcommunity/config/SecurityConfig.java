package com.juwon.springcommunity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
                .logout((logout) -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }
}
