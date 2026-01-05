package com.juwon.springcommunity.service;

import com.juwon.springcommunity.domain.ProductCategory;
import com.juwon.springcommunity.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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
     * 또한, 하위 카테고리와 '이름이 동일한' 다른 카테고리 ID도 함께 반환하여
     * 사용자가 다른 대분류의 동일 명칭 소분류에 등록했더라도 조회되도록 합니다.
     * (BFS 방식을 사용하여 손자 카테고리까지 모두 포함)
     * @param categoryId 기준 카테고리 ID
     * @return 카테고리 ID 목록 (본인 + 자손들 + 자손과 이름 같은 타 카테고리 포함)
     */
    public List<Long> getCategoryAndChildIds(Long categoryId) {
        List<ProductCategory> allCategories = productCategoryRepository.findAll();

        // 1. 데이터 전처리: ID별 맵, 이름별 ID 목록 맵, 부모별 자식 리스트 맵
        Map<Long, ProductCategory> idMap = new HashMap<>();
        Map<String, List<Long>> nameMap = new HashMap<>();
        Map<Long, List<Long>> parentMap = new HashMap<>();

        for (ProductCategory cat : allCategories) {
            idMap.put(cat.getId(), cat);
            nameMap.computeIfAbsent(cat.getName(), k -> new ArrayList<>()).add(cat.getId());
            if (cat.getParentId() != null) {
                parentMap.computeIfAbsent(cat.getParentId(), k -> new ArrayList<>()).add(cat.getId());
            }
        }

        Set<Long> resultIds = new HashSet<>();
        Queue<Long> queue = new LinkedList<>();

        // 시작점 설정
        if (idMap.containsKey(categoryId)) {
            resultIds.add(categoryId);
            queue.add(categoryId);
        }

        // BFS 탐색
        while (!queue.isEmpty()) {
            Long currentId = queue.poll();
            ProductCategory currentCat = idMap.get(currentId);

            // 1. 직계 자식들 추가
            List<Long> children = parentMap.get(currentId);
            if (children != null) {
                for (Long childId : children) {
                    if (resultIds.add(childId)) { // 방문 안 했으면 추가
                        queue.add(childId);
                    }
                }
            }

            // 2. 이름이 같은 다른 카테고리 추가 (확장 검색)
            // 현재 카테고리와 이름이 같은 다른 카테고리들도 검색 범위에 포함시킵니다.
            // (예: '수입명품 > 여성신발' 조회 시 '패션잡화 > 여성신발'도 포함)
            if (currentCat != null) {
                List<Long> sameNameIds = nameMap.get(currentCat.getName());
                if (sameNameIds != null) {
                    for (Long sameId : sameNameIds) {
                        // 이미 찾은 ID가 아니면 큐에 추가하여 그 자식들도 탐색하게 함
                        if (resultIds.add(sameId)) {
                            queue.add(sameId);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(resultIds);
    }

    public ProductCategory getCategoryById(Long id) {
        return productCategoryRepository.findById(id);
    }
}
