package com.juwon.springcommunity.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoom {

    private Long id;
    private Product product;
    private User buyer;
    private User seller;
    private LocalDateTime createdAt;

    public ChatRoom(Product product, User buyer, User seller) {
        this.product = product;
        this.buyer = buyer;
        this.seller = seller;
        this.createdAt = LocalDateTime.now();
    }
}
