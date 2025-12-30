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
    private Long categoryId;
    private ProductCategory category;
    private List<ProductImage> images; // 1:N 관계
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void setCategory(ProductCategory category) {
        this.category = category;
        if (category != null) {
            this.categoryId = category.getId();
        }
    }

    public void update(String title, String content, int price, String dealRegion, Long categoryId) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.dealRegion = dealRegion;
        this.categoryId = categoryId;
    }
}
