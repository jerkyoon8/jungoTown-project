package com.juwon.springcommunity.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    private Long id;
    private Long orderId;
    private String impUid; // 포트원 결제 고유 번호
    private String payMethod;
    private BigDecimal amount;
    private String status; // paid, ready, failed, cancelled
    private LocalDateTime paidAt;
}
