package com.juwon.springcommunity.dto;

import com.juwon.springcommunity.domain.ProductCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProductUpdateRequestDto {

    private String title;
    private String content;
    private int price;
    private String dealRegion;
    private Long categoryId;

    private List<MultipartFile> imageFiles; // 새로 추가할 이미지
    private List<Long> deleteImageIds; // 삭제할 이미지 ID 목록
}
