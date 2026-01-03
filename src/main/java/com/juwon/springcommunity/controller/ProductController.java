package com.juwon.springcommunity.controller;

import com.juwon.springcommunity.domain.ProductCategory;
import com.juwon.springcommunity.domain.Role;
import com.juwon.springcommunity.domain.User;
import com.juwon.springcommunity.dto.ProductCreateRequestDto;
import com.juwon.springcommunity.dto.ProductResponseDto;
import com.juwon.springcommunity.dto.ProductUpdateRequestDto;
import com.juwon.springcommunity.service.ProductService;
import com.juwon.springcommunity.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;


import com.juwon.springcommunity.service.RecentProductService;

@Controller
@RequestMapping("/products")
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final UserService userService;
    private final RecentProductService recentProductService;
    private final com.juwon.springcommunity.service.ProductCategoryService productCategoryService;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public ProductController(ProductService productService, UserService userService, RecentProductService recentProductService, com.juwon.springcommunity.service.ProductCategoryService productCategoryService, com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        this.productService = productService;
        this.userService = userService;
        this.recentProductService = recentProductService;
        this.productCategoryService = productCategoryService;
        this.objectMapper = objectMapper;
    }

    private String getCurrentUserEmail(Principal principal) {
        if (principal == null) return null;
        if (principal instanceof OAuth2AuthenticationToken) {
            return ((OAuth2AuthenticationToken) principal).getPrincipal().getAttribute("email");
        }
        return principal.getName();
    }

    // 전체 상품 목록을 보여줌.
    @GetMapping
    public String listProducts(Model model,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "20") int size,
                               @RequestParam(defaultValue = "latest") String sort,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "categoryId", required = false) Long categoryId,
                               @RequestParam(value = "minPrice", required = false) Integer minPrice,
                               @RequestParam(value = "maxPrice", required = false) Integer maxPrice) {
        
        Map<String, Object> response = productService.findPaginated(page, size, sort, keyword, categoryId, minPrice, maxPrice);
        model.addAttribute("products", response.get("products"));
        model.addAttribute("currentPage", response.get("currentPage"));
        model.addAttribute("totalPages", response.get("totalPages"));
        model.addAttribute("sort", sort); // 현재 정렬 기준을 모델에 추가
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId); // 현재 카테고리 ID를 모델에 추가
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        // 모든 카테고리 정보 추가
        model.addAttribute("allCategories", productCategoryService.getAllCategories());

        if (categoryId != null) {
            ProductCategory currentCategory = productCategoryService.getCategoryById(categoryId);
            model.addAttribute("currentCategory", currentCategory);
            
            // 부모 카테고리가 있다면 조회하여 추가
            if (currentCategory != null && currentCategory.getParentId() != null && currentCategory.getParentId() != 0) {
                ProductCategory parentCategory = productCategoryService.getCategoryById(currentCategory.getParentId());
                model.addAttribute("parentCategory", parentCategory);
            }
        }

        return "products/productList"; // resources/templates/products/list.html 을 렌더링
    }

    // 새 상품 생성 폼으로 연결
    @GetMapping("/new")
    public String showCreateForm(Model model) throws com.fasterxml.jackson.core.JsonProcessingException {
        model.addAttribute("product", new ProductCreateRequestDto()); // 빈 DTO 객체 전달
        String categoriesJson = objectMapper.writeValueAsString(productCategoryService.getAllCategories());
        model.addAttribute("categoriesJson", categoriesJson);
        model.addAttribute("isEdit", false);

        return "products/createProductForm";
    }

    // 새 상품 생성
    @PostMapping("/new")
    public String createProduct(@Valid @ModelAttribute("product") ProductCreateRequestDto productCreateRequestDto,
                             BindingResult bindingResult,
                             Principal principal,
                             Model model) throws IOException {

        // 입력값 검증에 실패하면 다시 폼으로 돌려보냄 (에러 메시지 포함)
        if (bindingResult.hasErrors()) {
            String categoriesJson = objectMapper.writeValueAsString(productCategoryService.getAllCategories());
            model.addAttribute("categoriesJson", categoriesJson);
            return "products/createProductForm";
        }

        String username = principal.getName();
        if (principal instanceof OAuth2AuthenticationToken) {
            username = ((OAuth2AuthenticationToken) principal).getPrincipal().getAttribute("email");
        }
        User user = userService.findUserByEmail(username);

        productService.createProduct(productCreateRequestDto, user.getId());

        return "redirect:/products";
    }

    // 상품 id 페이지로 연결
    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model, Principal principal, HttpSession session) {

        productService.checkIncreaseView(id, session); // 조회수 증가
        ProductResponseDto product = productService.findProductById(id);
        model.addAttribute("product", product);

        // === 최근 본 상품 기록 로직 추가 시작 ===
        String userIdentifier;
        if (principal != null) {
            // 로그인 사용자: User ID 사용
            String email = principal.getName();
            if (principal instanceof OAuth2AuthenticationToken) {
                email = ((OAuth2AuthenticationToken) principal).getPrincipal().getAttribute("email");
            }
            User user = userService.findUserByEmail(email);
            userIdentifier = "user:" + user.getId();
        } else {
            // 비로그인 사용자: Session ID 사용
            userIdentifier = "session:" + session.getId();
        }
        recentProductService.addRecentProduct(userIdentifier, id);
        // === 최근 본 상품 기록 로직 추가 끝 ===

        // 현재 보고 있는 상품이 로그인한 사용자가 작성한 것인지 확인 (수정/삭제 버튼 노출 여부 결정)
        boolean isOwner = false;
        if (principal != null) {
            String currentUserEmail = getCurrentUserEmail(principal);
            User currentUser = userService.findUserByEmail(currentUserEmail);
            String ownerEmail = userService.findEmailById(product.getUserId());
            
            // 작성자 본인이거나 관리자 권한이 있는 경우 true
            isOwner = (ownerEmail != null && ownerEmail.equals(currentUserEmail)) || currentUser.getRole() == Role.ADMIN;
        }
        model.addAttribute("isOwner", isOwner);

        return "products/productDetail";
    }

    // 상품 수정 폼으로 연결
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) throws com.fasterxml.jackson.core.JsonProcessingException {
        ProductResponseDto product = productService.findProductById(id);

        // 권한 확인: 작성자 본인이거나 관리자여야 함
        String currentUserEmail = getCurrentUserEmail(principal);
        User currentUser = userService.findUserByEmail(currentUserEmail);
        String ownerEmail = userService.findEmailById(product.getUserId());

        if (!(ownerEmail != null && ownerEmail.equals(currentUserEmail)) && currentUser.getRole() != Role.ADMIN) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        model.addAttribute("product", product);
        String categoriesJson = objectMapper.writeValueAsString(productCategoryService.getAllCategories());
        model.addAttribute("categoriesJson", categoriesJson);
        model.addAttribute("isEdit", true);
        return "products/createProductForm";
    }

    // 상품 수정
    @PostMapping("/{id}/edit")
    public String updateProduct(@PathVariable Long id, @ModelAttribute ProductUpdateRequestDto productUpdateRequestDto, Principal principal)
    throws IOException {
        ProductResponseDto product = productService.findProductById(id);

        // 권한 확인: 작성자 본인이거나 관리자여야 함
        String currentUserEmail = getCurrentUserEmail(principal);
        User currentUser = userService.findUserByEmail(currentUserEmail);
        String ownerEmail = userService.findEmailById(product.getUserId());

        if (!(ownerEmail != null && ownerEmail.equals(currentUserEmail)) && currentUser.getRole() != Role.ADMIN) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        productService.updateProduct(id, productUpdateRequestDto);
        return "redirect:/products/" + id; // 수정된 상품의 상세 페이지로 리다이렉트
    }

    // 상품 삭제
    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id, Principal principal) {
        ProductResponseDto product = productService.findProductById(id);

        String currentUserEmail = getCurrentUserEmail(principal);
        User user = userService.findUserByEmail(currentUserEmail);

        // 작성자 본인 확인 또는 관리자 권한 확인
        if (!product.getUserId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        productService.deleteProduct(id);
        return "redirect:/products"; // 목록 페이지로 리다이렉트
    }

    // 찜하기
    @PostMapping("/{id}/wishlist")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> increaseWishlistCount(@PathVariable Long id) {
        productService.increaseWishlistCount(id);
        ProductResponseDto updatedProduct = productService.findProductById(id);
        int newWishlistCount = updatedProduct.getWishlistCount();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("newWishlistCount", newWishlistCount);
        return ResponseEntity.ok(response);
    }
}