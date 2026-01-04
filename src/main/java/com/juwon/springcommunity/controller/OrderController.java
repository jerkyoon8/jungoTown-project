package com.juwon.springcommunity.controller;

import com.juwon.springcommunity.domain.Order;
import com.juwon.springcommunity.security.oauth.SessionUser;
import com.juwon.springcommunity.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

import com.juwon.springcommunity.domain.User;
import com.juwon.springcommunity.service.UserService;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final HttpSession httpSession;

    @PostMapping("/api/orders")
    @ResponseBody
    public ResponseEntity<String> createOrder(@RequestParam Long productId) {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (sessionUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        
        // SessionUser에는 최소 정보만 있으므로, 필요하다면 DB에서 User를 다시 조회
        User user = userService.findUserByEmail(sessionUser.getEmail());

        Order order = orderService.createOrder(user, productId);
        
        return ResponseEntity.ok(order.getOrderUid());
    }
}
