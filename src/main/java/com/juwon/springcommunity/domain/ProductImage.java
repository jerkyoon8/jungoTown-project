package com.juwon.springcommunity.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    private Long id; // 이미지 ID (PK)
    private Long productId; // 상품 ID (FK)
    private String originalFileName; // 원본 파일명
    private String storedFileName; // 서버에 저장될 파일명
    private String filePath; // 서버에 저장된 파일 경로
    private Long fileSize; // 파일 크기
    private LocalDateTime createdDate; // 생성일시
    private LocalDateTime modifiedDate; // 수정일시
}
