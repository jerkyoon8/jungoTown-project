package com.juwon.springcommunity.controller;

import com.juwon.springcommunity.domain.User;
import com.juwon.springcommunity.dto.RecentProductDto;
import com.juwon.springcommunity.service.RecentProductService;
import com.juwon.springcommunity.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final RecentProductService recentProductService;
    private final UserService userService;
    private final com.juwon.springcommunity.service.ProductCategoryService productCategoryService;
    private final com.juwon.springcommunity.service.CarouselService carouselService;
    private final com.juwon.springcommunity.service.ProductService productService;

    // index 페이지를 연결
    @GetMapping("/")
    public String home(Model model, Principal principal, HttpSession session) {

        // === (기존) 최근 본 상품 목록 조회 로직 추가 시작 ===     
        String userIdentifier;
        if (principal != null) {
            String email = principal.getName();
            if (principal instanceof OAuth2AuthenticationToken) {
                // OAuth2 로그인인 경우 email 속성을 가져옴
                email = ((OAuth2AuthenticationToken) principal).getPrincipal().getAttribute("email");
            }
            User user = userService.findUserByEmail(email);
            userIdentifier = "user:" + user.getId();
        } else {
            userIdentifier = "session:" + session.getId();
        }

        List<RecentProductDto> recentProducts = recentProductService.getRecentProducts(userIdentifier);
        model.addAttribute("recentProducts", recentProducts);
        // === 최근 본 상품 목록 조회 로직 추가 끝 ===


        // === (추가) 카테고리 목록 조회 로직 시작 ===
        List<com.juwon.springcommunity.domain.ProductCategory> categories = productCategoryService.getAllCategories();
        model.addAttribute("categories", categories);
        // === 카테고리 목록 조회 로직 끝 ===


        // === (추가) 캐러셀 데이터 조회 로직 시작 ===
        List<com.juwon.springcommunity.domain.CarouselItem> carouselItems = carouselService.findActiveItems();
        model.addAttribute("carouselItems", carouselItems);
        // === 캐러셀 데이터 조회 로직 끝 ===

        // === (추가) 인기 상품 조회 로직 시작 ===
        List<com.juwon.springcommunity.dto.ProductResponseDto> popularProducts = productService.getPopularProducts(8);
        model.addAttribute("popularProducts", popularProducts);
        // === 인기 상품 조회 로직 끝 ===


        return "index";
    }

    @GetMapping("/test")
    public String testPage() {

        return "tes";
    }

    @GetMapping("/chat")
    public String chatPage() {
        return "chat";
    }
}
