package com.juwon.springcommunity.service;

import com.juwon.springcommunity.domain.Product;
import com.juwon.springcommunity.dto.WishListResponseDto;
import com.juwon.springcommunity.repository.ProductImageRepository;
import com.juwon.springcommunity.repository.ProductRepository;
import com.juwon.springcommunity.repository.ProductWishListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductWishListService {

    private final ProductWishListRepository productWishListRepository;
    private final ProductRepository productRepository; // 상품 존재 여부 확인을 위해 추가
    private final ProductImageRepository productImageRepository;


    // 찜하기 토글 (추가 또는 취소)
    @Transactional
    public boolean toggleWishlist(Long userId, Long productId) {
        // 상품 존재 검사
        productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다. id: " + productId));

        // 1. 찜하기가 이미 존재하는지 확인
        int count = productWishListRepository.checkWishList(userId, productId);

        // 2. 이미 존재하면 삭제하고 false 반환 (찜 취소)
        if (count > 0) {
            productWishListRepository.delete(userId, productId);
            productRepository.decreaseWishlistCount(productId);
            return false;
        } 
        
        // 3. 존재하지 않으면 추가하고 true 반환 (찜 하기)
        else {
            productWishListRepository.save(userId, productId);
            productRepository.increaseWishlistCount(productId);
            return true;
        }
    }

    // 찜 여부 확인
    public boolean isWishlisted(Long userId, Long productId) {
        return productWishListRepository.checkWishList(userId, productId) > 0;
    }

    // 찜 목록 조회
    public List<WishListResponseDto> getWishList(Long userId) {
        List<Product> products = productWishListRepository.findWishListByUserId(userId);
        return products.stream()
                .map(product -> {
                    return new WishListResponseDto(product, productImageRepository.findByProductId(product.getId()));
                })
                .collect(Collectors.toList());
    }
}
