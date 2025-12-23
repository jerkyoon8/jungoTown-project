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

    // index 페이지를 연결
    @GetMapping("/")
    public String home(Model model, Principal principal, HttpSession session) {

        // === (유지) 최근 본 상품 목록 조회 로직 추가 시작 ===
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


        // === (추가) 캐러셀 아이템 생성 로직 시작 ===
        List<Map<String, String>> carouselItems = new ArrayList<>();

        Map<String, String> item1 = new HashMap<>();
        item1.put("image", "/image/carousel/carousel-1.png");
        item1.put("title", "신선한 과일의 세계");
        item1.put("description", "제철을 맞은 신선한 과일들을 만나보세요.");
        carouselItems.add(item1);

        Map<String, String> item2 = new HashMap<>();
        item2.put("image", "/image/carousel/carousel-2.png");
        item2.put("title", "다채로운 채소 모음");
        item2.put("description", "건강한 식단을 위한 최고의 선택!");
        carouselItems.add(item2);

        Map<String, String> item3 = new HashMap<>();
        item3.put("image", "/image/carousel/carousel-3.png");
        item3.put("title", "오늘의 특별 할인");
        item3.put("description", "놓치면 후회할 특별한 가격의 상품들을 확인하세요.");
        carouselItems.add(item3);

        model.addAttribute("carouselItems", carouselItems);
        // === 캐러셀 아이템 생성 로직 끝 ===


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
