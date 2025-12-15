package com.juwon.springcommunity.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductWishList {

    private Long id;
    private Long userId;
    private Long productId;
}
