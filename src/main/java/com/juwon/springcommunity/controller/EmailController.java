package com.juwon.springcommunity.controller;

import com.juwon.springcommunity.service.EmailService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;
    private final com.juwon.springcommunity.service.UserService userService;

    // 인증 번호 전송 API
    @PostMapping("/send")
    public ResponseEntity<String> sendVerificationCode(@RequestBody EmailRequest request) {
        if (userService.isEmailDuplicate(request.getEmail())) {
            return ResponseEntity.badRequest().body("이미 가입된 이메일입니다.");
        }
        emailService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok("인증 번호가 전송되었습니다.");
    }

    // 인증 번호 검증 API
    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@RequestBody EmailVerifyRequest request) {
        boolean isVerified = emailService.verifyCode(request.getEmail(), request.getCode());

        if (isVerified) {
            return ResponseEntity.ok("인증에 성공했습니다.");
        } else {
            return ResponseEntity.badRequest().body("인증 번호가 일치하지 않거나 만료되었습니다.");
        }
    }

    // DTO 클래스들 (간단하므로 내부에 정의)
    @Data
    public static class EmailRequest {
        private String email;
    }

    @Data
    public static class EmailVerifyRequest {
        private String email;
        private String code;
    }
}
