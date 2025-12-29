package com.juwon.springcommunity.service;

import com.juwon.springcommunity.domain.ProductCategory;
import com.juwon.springcommunity.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    /**
     * 모든 카테고리를 계층형 구조(트리)로 변환하여 반환합니다.
     * @return 최상위 카테고리 리스트 (자식들은 children 필드에 포함됨)
     */
    public List<ProductCategory> getAllCategories() {
        // 1. DB에서 모든 카테고리 조회 (평문 리스트)
        List<ProductCategory> allCategories = productCategoryRepository.findAll();

        // 2. Map을 사용하여 ID로 쉽게 접근할 수 있도록 변환
        Map<Long, ProductCategory> categoryMap = new HashMap<>();
        for (ProductCategory category : allCategories) {
            categoryMap.put(category.getId(), category);
        }

        // 3. 부모-자식 관계 연결
        List<ProductCategory> rootCategories = new ArrayList<>();

        for (ProductCategory category : allCategories) {
            if (category.getParentId() == null || category.getParentId() == 0) {
                // 부모가 없으면 최상위 카테고리
                rootCategories.add(category);
            } else {
                // 부모가 있으면 부모 카테고리를 찾아서 children 리스트에 추가
                ProductCategory parent = categoryMap.get(category.getParentId());
                if (parent != null) {
                    parent.getChildren().add(category);
                }
            }
        }

        return rootCategories;
    }
}
