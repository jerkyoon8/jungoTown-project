package com.juwon.springcommunity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentCallbackDto {
    private String payment_uid; // 결제 고유 번호 (imp_uid)
    private String order_uid;   // 주문 고유 번호 (merchant_uid)
}
