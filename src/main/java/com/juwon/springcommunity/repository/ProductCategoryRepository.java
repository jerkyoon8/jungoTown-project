package com.juwon.springcommunity.repository;

import com.juwon.springcommunity.domain.ProductCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductCategoryRepository {
    List<ProductCategory> findAll();
    ProductCategory findById(Long id);
}
