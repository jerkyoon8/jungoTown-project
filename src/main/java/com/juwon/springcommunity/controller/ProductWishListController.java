package com.juwon.springcommunity.controller;

import com.juwon.springcommunity.domain.User;
import com.juwon.springcommunity.service.ProductWishListService;
import com.juwon.springcommunity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class ProductWishListController {

    private final ProductWishListService productWishListService;
    private final UserService userService;

    // 상품의 찜하기를 추가한다.
    @PostMapping("/wishlist/{productId}")
    public ResponseEntity<Map<String, String>> addWishlist(@PathVariable Long productId, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        Long userId = user.getId();

        try {
            boolean isAdded = productWishListService.addWishlist(userId, productId);

            if (isAdded) {
                return ResponseEntity.ok(Map.of("message", "찜 목록에 추가되었습니다."));
            } else {
                // 중복된 경우 (400 Bad Request)
                return ResponseEntity.badRequest().body(Map.of("message", "이미 찜한 상품입니다."));
            }
        } catch (IllegalArgumentException e) {
            // 서비스에서 상품이 존재하지 않을 경우 던진 예외를 처리합니다.
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}
