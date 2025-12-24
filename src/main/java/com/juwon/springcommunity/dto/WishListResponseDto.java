package com.juwon.springcommunity.dto;

import com.juwon.springcommunity.domain.Product;
import com.juwon.springcommunity.domain.ProductImage;
import lombok.Getter;

import java.util.List;

@Getter
public class WishListResponseDto {

    private Long id;
    private String title;
    private int price;
    private String thumbnailUrl;

    public WishListResponseDto(Product product, List<ProductImage> images) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.price = product.getPrice();
        
        // 첫 번째 이미지를 썸네일로 사용, 없으면 placeholder 사용
        if (images != null && !images.isEmpty()) {
            this.thumbnailUrl = "/image/" + images.get(0).getStoredFileName();
        } else {
            this.thumbnailUrl = "/image/placeholder.png";
        }
    }
}
