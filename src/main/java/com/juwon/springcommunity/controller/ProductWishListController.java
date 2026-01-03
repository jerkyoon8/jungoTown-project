package com.juwon.springcommunity.controller;

import com.juwon.springcommunity.domain.User;
import com.juwon.springcommunity.dto.ProductResponseDto;
import com.juwon.springcommunity.dto.WishListResponseDto;
import com.juwon.springcommunity.service.ProductService;
import com.juwon.springcommunity.service.ProductWishListService;
import com.juwon.springcommunity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class ProductWishListController {

    private final ProductWishListService productWishListService;
    private final UserService userService;
    private final ProductService productService;

    // 찜하기 토글 (추가/취소)
    @PostMapping("/wishlist/{productId}")
    public ResponseEntity<Map<String, Object>> toggleWishlist(@PathVariable Long productId, Principal principal) {
        String username = principal.getName();
        if (principal instanceof OAuth2AuthenticationToken) {
            username = ((OAuth2AuthenticationToken) principal).getPrincipal().getAttribute("email");
        }
        User user = userService.findUserByEmail(username);
        Long userId = user.getId();

        try {
            boolean isWished = productWishListService.toggleWishlist(userId, productId);
            
            // 업데이트된 찜 개수 조회
            ProductResponseDto product = productService.findProductById(productId);
            int newWishlistCount = product.getWishlistCount();

            Map<String, Object> response = new HashMap<>();
            response.put("wished", isWished);
            response.put("wishlistCount", newWishlistCount);
            response.put("message", isWished ? "찜 목록에 추가되었습니다." : "찜 목록에서 삭제되었습니다.");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 찜 목록 조회 API
    @GetMapping("/api/wishlist")
    public ResponseEntity<List<WishListResponseDto>> getWishList(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build(); // 로그인 필요
        }
        String username = principal.getName();
        if (principal instanceof OAuth2AuthenticationToken) {
            username = ((OAuth2AuthenticationToken) principal).getPrincipal().getAttribute("email");
        }
        User user = userService.findUserByEmail(username);
        
        List<WishListResponseDto> wishList = productWishListService.getWishList(user.getId());
        return ResponseEntity.ok(wishList);
    }
}
