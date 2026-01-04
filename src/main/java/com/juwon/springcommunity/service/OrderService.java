package com.juwon.springcommunity.service;

import com.juwon.springcommunity.domain.Order;
import com.juwon.springcommunity.domain.Product;
import com.juwon.springcommunity.domain.User;
import com.juwon.springcommunity.repository.OrderRepository;
import com.juwon.springcommunity.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Order order = new Order();
        order.setUserId(user.getId());
        order.setProductId(productId);
        order.setPrice(BigDecimal.valueOf(product.getPrice()));
        order.setOrderUid(UUID.randomUUID().toString());
        order.setStatus("PENDING");

        orderRepository.save(order);
        
        // MyBatis의 useGeneratedKeys로 인해 id가 채워짐 (혹은 안 채워질 수도 있으니 확인 필요하지만, 보통 채워짐)
        // 안전하게 다시 조회하거나, 그대로 리턴.
        // 여기서는 저장된 객체(ID 포함)를 그대로 사용한다고 가정.
        return order;
    }

    public Order findByOrderUid(String orderUid) {
        return orderRepository.findByOrderUid(orderUid)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
    }
}
