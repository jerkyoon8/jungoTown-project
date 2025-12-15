package com.juwon.springcommunity.service;

import com.juwon.springcommunity.domain.Product;
import com.juwon.springcommunity.repository.ProductRepository;
import com.juwon.springcommunity.repository.ProductWishListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductWishListService {

    private final ProductWishListRepository productWishListRepository;
    private final ProductRepository productRepository; // 상품 존재 여부 확인을 위해 추가


    // 찜하기를 추가한다.
    @Transactional
    public boolean addWishlist(Long userId, Long productId) {

        /*
        * 위시 리스트의 경우 
        * 1. 상품 존재 검사
        * 2. 찜하기 존재 검사
        * 3.
        * 
        * */
        
        
        // 상품이 존재하는지 검사. 없으면 IllegalArgumentException 발생
        Optional<Product> byId = productRepository.findById(productId);
            byId.orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다. id: " + productId));

        log.info("product ={},",byId.get());


        // 1. 찜하기가 이미 존재하는지 확인
        int count = productWishListRepository.checkWishList(userId, productId);

        // 2. 존재하지 않으면 새로 추가하고 true 반환
        if (count == 0) {
            productWishListRepository.save(userId, productId);
            productRepository.increaseWishlistCount(productId); // 찜하기 개수 1 증가
            return true; // 새로 추가됨
        }

        // 3. 이미 존재하면 false 반환
        return false;
    }
}
