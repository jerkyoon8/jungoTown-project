package com.juwon.springcommunity.repository;

import com.juwon.springcommunity.domain.ProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductImageRepository {

    // 여러 이미지 정보를 한 번에 저장
    void saveAll(List<ProductImage> productImages);

    // 상품 ID로 이미지 목록 조회
    List<ProductImage> findByProductId(Long productId);

    // 상품 ID로 이미지 정보 삭제
    void deleteByProductId(Long productId);

    // 이미지 ID로 이미지 정보 삭제
    void save(ProductImage productImage);

    List<ProductImage> findFirstImagesByProductIds(@Param("productIds") List<Long> productIds);

}
