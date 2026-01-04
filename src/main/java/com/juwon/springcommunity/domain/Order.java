package com.juwon.springcommunity.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Order {
    private Long id;
    private Long userId;
    private Long productId;
    private String orderUid; // 주문 고유 번호 (merchant_uid)
    private BigDecimal price;
    private String status; // PENDING, PAID, CANCELLED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
