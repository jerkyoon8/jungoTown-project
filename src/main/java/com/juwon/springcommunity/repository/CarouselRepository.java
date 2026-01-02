package com.juwon.springcommunity.repository;

import com.juwon.springcommunity.domain.CarouselItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CarouselRepository {
    List<CarouselItem> findAll();
    List<CarouselItem> findActiveItems();
    CarouselItem findById(Long id);
    void save(CarouselItem carouselItem);
    void update(CarouselItem carouselItem);
    void delete(Long id);
}
