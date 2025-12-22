package com.juwon.springcommunity.repository;

import com.juwon.springcommunity.domain.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Mapper
public interface ProductRepository {

    // 상품 저장
    void save(Product product);

    // id로 상품 조회
    Optional<Product> findById(Long id);

    // 모든 상품 조회
    List<Product> findAll(@Param("keyword") String keyword);

    // 상품 정보 수정
    void update(Product product);

    // id로 상품 찾아서 삭제
    void deleteById(Long id);

    // 조회수 증가
    void increaseViews(Long id);

    // 찜하기 증가
    void increaseWishlistCount(Long id);

    // 모든 상품 수 조회
    long countAll(@Param("keyword") String keyword);

    // 페이징 처리하여 상품 조회
    List<Product> findWithPaging(Map<String, Object> params);

    // ID 목록으로 상품 목록 조회
    List<Product> findByIdIn(@Param("ids") List<Long> ids);


}