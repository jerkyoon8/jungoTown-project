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

    /**
     * 특정 카테고리 ID와 그 하위 카테고리 ID들의 목록을 반환합니다.
     * @param categoryId 기준 카테고리 ID
     * @return 카테고리 ID 목록 (본인 + 자식들)
     */
    public List<Long> getCategoryAndChildIds(Long categoryId) {
        List<ProductCategory> allCategories = productCategoryRepository.findAll();
        List<Long> ids = new ArrayList<>();
        
        // 본인 추가
        ids.add(categoryId);
        
        // 자식 추가 (1단계만 고려. 다중 계층일 경우 재귀 필요하지만 현재 구조상 2단계로 가정)
        for (ProductCategory category : allCategories) {
            if (categoryId.equals(category.getParentId())) {
                ids.add(category.getId());
            }
        }
        
        return ids;
    }
}
