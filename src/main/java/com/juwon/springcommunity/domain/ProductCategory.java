package com.juwon.springcommunity.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductCategory {

    private Long id;
    private String name;
    private Long parentId;
    private List<ProductCategory> children = new ArrayList<>();

    public ProductCategory(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
    }
}