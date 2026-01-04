package com.juwon.springcommunity.controller;

import com.juwon.springcommunity.dto.PaymentCallbackDto;
import com.juwon.springcommunity.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/api/payments/validation")
    @ResponseBody
    public ResponseEntity<String> validatePayment(@RequestBody PaymentCallbackDto dto) {
        try {
            paymentService.validatePayment(dto);
            return ResponseEntity.ok("결제 검증 및 저장 성공");
        } catch (IllegalArgumentException e) {
            log.error("결제 검증 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("결제 처리 중 서버 에러", e);
            return ResponseEntity.internalServerError().body("서버 에러 발생");
        }
    }
}
