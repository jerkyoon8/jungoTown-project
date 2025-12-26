package com.juwon.springcommunity.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime; // 추가
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Product {

    private Long id;
    private Long userId; // 유저 id 외래키
    private String title;
    private String content;
    private int price; // 가격
    private String dealRegion; // 거래 희망 지역
    private int views; // 조회수
    private int wishlistCount; // 찜하기 수
    private boolean isDeleted; // 삭제 여부
    private ProductCategory category;
    private List<ProductImage> images; // 1:N 관계
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void update(String title, String content, int price, String dealRegion, ProductCategory category) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.dealRegion = dealRegion;
        this.category = category;
    }
}
