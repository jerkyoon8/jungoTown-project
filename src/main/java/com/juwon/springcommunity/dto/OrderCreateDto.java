package com.juwon.springcommunity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderCreateDto {
    private Long productId;
    // 수량이나 옵션 등이 필요하면 여기에 추가
}
