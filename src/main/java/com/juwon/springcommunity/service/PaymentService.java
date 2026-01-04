package com.juwon.springcommunity.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juwon.springcommunity.domain.Order;
import com.juwon.springcommunity.domain.Payment;
import com.juwon.springcommunity.dto.PaymentCallbackDto;
import com.juwon.springcommunity.repository.OrderRepository;
import com.juwon.springcommunity.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${portone.api.secret}")
    private String apiSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void validatePayment(PaymentCallbackDto dto) throws IOException {
        String paymentId = dto.getPayment_uid();
        String orderId = dto.getOrder_uid();
        
        // 1. 포트원 결제내역 단건 조회 (V2)
        String url = "https://api.portone.io/payments/" + paymentId;
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "PortOne " + apiSecret);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("포트원 API 호출 실패: " + response.getStatusCode());
        }

        // 2. 응답 파싱
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        
        // V2 응답 구조에 따라 파싱 (status, amount.total 등 확인)
        String status = rootNode.path("status").asText();
        long totalAmount = rootNode.path("amount").path("total").asLong();

        // 3. 결제 검증
        if (!"PAID".equals(status)) {
             throw new RuntimeException("결제가 완료되지 않았습니다. 상태: " + status);
        }

        // 주문 금액 비교
        Order order = orderRepository.findByOrderUid(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));

        if (order.getPrice().compareTo(BigDecimal.valueOf(totalAmount)) != 0) {
            throw new RuntimeException("결제 금액이 일치하지 않습니다. (주문: " + order.getPrice() + ", 결제: " + totalAmount + ")");
        }

        // 4. 결제 정보 저장 및 주문 상태 업데이트
        Payment payment = Payment.builder()
                .impUid(paymentId)
                .orderId(order.getId())
                .amount(BigDecimal.valueOf(totalAmount))
                .status("PAID")
                .build();
        
        paymentRepository.save(payment);
        
        // 주문 상태 변경 (PENDING -> PAID)
        order.setStatus("PAID");
        orderRepository.update(order);
    }
}