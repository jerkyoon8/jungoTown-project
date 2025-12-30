package com.juwon.springcommunity.dto;

import com.juwon.springcommunity.domain.Product;
import com.juwon.springcommunity.domain.ProductCategory;
import com.juwon.springcommunity.domain.ProductImage;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter

public class ProductResponseDto {

    private final Long id;
    private final String title;
    private final String content;
    private final int price;
    private final String dealRegion;
    private final int views;
    private final int wishlistCount;

    private final Long userId;
    private final String author; // 작성자 닉네임

    private final ProductCategory category;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private final List<String> storedFileNames; // 이미지 파일 이름 목록


    public Long getCategoryId() {
        return category != null ? category.getId() : null;
    }

    public ProductResponseDto(Product product, String author, List<ProductImage> images) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.content = product.getContent();
        this.price = product.getPrice();
        this.dealRegion = product.getDealRegion();
        this.views = product.getViews();
        this.wishlistCount = product.getWishlistCount();


        // null 발생 가능성 (EXCEPTION) 추가 필수
        this.userId = product.getUserId();
        this.author = author;

        this.category = product.getCategory();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
        // PostImage 객체 리스트에서 String 만 추출하여 리스트로 변환
        this.storedFileNames = images.stream()
                .map(ProductImage::getStoredFileName)
                .toList();
    }

    // post-> ResponseDTO 변환 메서드. (static)
    public static ProductResponseDto of(Product product, String nickname, List<ProductImage> images) {
        return new ProductResponseDto(product, nickname, images);
    }




}