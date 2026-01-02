package com.juwon.springcommunity.service;

import com.juwon.springcommunity.domain.CarouselItem;
import com.juwon.springcommunity.repository.CarouselRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarouselService {

    private final CarouselRepository carouselRepository;

    public List<CarouselItem> findAll() {
        return carouselRepository.findAll();
    }

    public List<CarouselItem> findActiveItems() {
        return carouselRepository.findActiveItems();
    }

    public CarouselItem findById(Long id) {
        return carouselRepository.findById(id);
    }

    @Transactional
    public void save(CarouselItem carouselItem) {
        if (carouselItem.getSortOrder() == null) {
            carouselItem.setSortOrder(0);
        }
        if (carouselItem.getIsActive() == null) {
            carouselItem.setIsActive(true);
        }
        carouselRepository.save(carouselItem);
    }

    @Transactional
    public void update(CarouselItem carouselItem) {
        carouselRepository.update(carouselItem);
    }

    @Transactional
    public void delete(Long id) {
        carouselRepository.delete(id);
    }
}
