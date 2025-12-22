package com.juwon.springcommunity.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 최근 본 상품을 간단히 표시하기 위한 DTO
 */
@Getter
@Setter
public class RecentProductDto {

    private Long id;
    private String title;
    private int price;
    private String representativeImageUrl; // 대표 이미지 1개의 URL
}
