package com.juwon.springcommunity.service;

import com.juwon.springcommunity.domain.Product;
import com.juwon.springcommunity.domain.ProductImage;
import com.juwon.springcommunity.dto.RecentProductDto;
import com.juwon.springcommunity.repository.ProductImageRepository;
import com.juwon.springcommunity.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecentProductService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private static final long MAX_RECENT_PRODUCTS = 20;

    public void addRecentProduct(String userIdentifier, Long productId) {
        String key = "recent_product:" + userIdentifier;
        ListOperations<String, String> listOps = redisTemplate.opsForList();

        // 중복 제거
        listOps.remove(key, 0, productId.toString());
        // 맨 앞에 추가
        listOps.leftPush(key, productId.toString());
        // 리스트 크기 제한
        listOps.trim(key, 0, MAX_RECENT_PRODUCTS - 1);
    }

    public List<RecentProductDto> getRecentProducts(String userIdentifier) {
        String key = "recent_product:" + userIdentifier;
        ListOperations<String, String> listOps = redisTemplate.opsForList();

        // 1. Redis에서 상품 ID 목록 조회 (최대 10개)
        List<String> productIdsStr = listOps.range(key, 0, 9);
        if (productIdsStr == null || productIdsStr.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> productIds = productIdsStr.stream().map(Long::valueOf).toList();

        // 2. DB에서 상품 정보 일괄 조회
        Map<Long, Product> productMap = productRepository.findByIdIn(productIds).stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        // 3. DB에서 대표 이미지 정보 일괄 조회
        Map<Long, String> imageMap = productImageRepository.findFirstImagesByProductIds(productIds).stream()
                .collect(Collectors.toMap(ProductImage::getProductId, ProductImage::getStoredFileName));

        // 4. DTO 조립 (Redis 순서 유지)
        return productIds.stream()
                .map(id -> {
                    Product product = productMap.get(id);
                    if (product == null) return null;

                    RecentProductDto dto = new RecentProductDto();
                    dto.setId(product.getId());
                    dto.setTitle(product.getTitle());
                    dto.setPrice(product.getPrice());
                    // 이미지 경로를 완성하여 DTO에 설정 (FileStore 로직이 필요하다면 추가)
                    dto.setRepresentativeImageUrl(imageMap.get(id)); 
                    return dto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
