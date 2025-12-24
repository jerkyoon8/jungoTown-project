package com.juwon.springcommunity.repository;

import com.juwon.springcommunity.domain.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductWishListRepository {

    /**
     * userId와 productId로 찜하기 존재 여부를 확인합니다.
     * @param userId 사용자 ID
     * @param productId 상품 ID
     * @return 존재하면 1 이상, 존재하지 않으면 0
     */
    int checkWishList(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 새로운 찜하기를 추가합니다.
     * @param userId 사용자 ID
     * @param productId 상품 ID
     */
    void save(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 사용자가 찜한 상품 목록을 조회합니다.
     * @param userId 사용자 ID
     * @return 찜한 상품 리스트
     */
    List<Product> findWishListByUserId(@Param("userId") Long userId);
}
