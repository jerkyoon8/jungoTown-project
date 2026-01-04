package com.juwon.springcommunity.repository;

import com.juwon.springcommunity.domain.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentRepository {
    void save(Payment payment);
}
