package com.juwon.springcommunity.repository;

import com.juwon.springcommunity.domain.Order;
import org.apache.ibatis.annotations.Mapper;
import java.util.Optional;

@Mapper
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(Long id);
    Optional<Order> findByOrderUid(String orderUid);
    void update(Order order);
}
