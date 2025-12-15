package com.juwon.springcommunity.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}
