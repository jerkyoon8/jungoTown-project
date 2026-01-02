package com.juwon.springcommunity.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarouselItem {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String linkUrl;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
