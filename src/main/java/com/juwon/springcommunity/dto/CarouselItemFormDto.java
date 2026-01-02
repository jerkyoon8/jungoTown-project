package com.juwon.springcommunity.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CarouselItemFormDto {
    private Long id;
    private String title;
    private String description;
    private String linkUrl;
    private Integer sortOrder;
    private Boolean isActive;
    private MultipartFile imageFile;
    private String savedImageUrl; // 수정 시 기존 이미지 경로 표시용
}
