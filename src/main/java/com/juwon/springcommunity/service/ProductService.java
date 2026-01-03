package com.juwon.springcommunity.service;

import com.juwon.springcommunity.domain.Product;
import com.juwon.springcommunity.domain.ProductImage;
import com.juwon.springcommunity.domain.User;
import com.juwon.springcommunity.dto.ProductCreateRequestDto;
import com.juwon.springcommunity.dto.ProductResponseDto;
import com.juwon.springcommunity.dto.ProductUpdateRequestDto;
import com.juwon.springcommunity.dto.UserResponseDto;
import com.juwon.springcommunity.repository.ProductImageRepository;
import com.juwon.springcommunity.repository.ProductRepository;
import com.juwon.springcommunity.util.FileStore;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final UserService userService;
    private final FileStore fileStore;
    private final ProductCategoryService productCategoryService;

    public ProductService(ProductRepository productRepository, ProductImageRepository productImageRepository, UserService userService, FileStore fileStore, ProductCategoryService productCategoryService) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.userService = userService;
        this.fileStore = fileStore;
        this.productCategoryService = productCategoryService;
    }

    // 상품과 이미지를 함께 저장합니다.
    @Transactional
    public ProductResponseDto createProduct(ProductCreateRequestDto createDto, Long userId)
            throws IOException {
        //  상품을 저장
        Product product = createDto.toEntity(userId);
        productRepository.save(product);

        //  이미지 파일을 저장하고 DB 에 저장
        List<ProductImage> images = fileStore.storeFiles(createDto.getImageFiles(), product.getId(), product.getCategoryId());
        if (!images.isEmpty()) {
            productImageRepository.saveAll(images);
        }

        //  응답 DTO를 생성하여 반환합니다.
        UserResponseDto userDto = userService.findUserById(userId);
        return ProductResponseDto.of(product, userDto.getNickname(), images);
    }

    // 모든 상품을 조회 (이미지 포함)
    public List<ProductResponseDto> findAllProducts() {
        List<Product> products = productRepository.findAll(null);
        Set<Long> userIds = products.stream().map(Product::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = findUsersMapByIds(userIds);

        return products.stream()
                .map(product -> {
                    User author = userMap.get(product.getUserId());
                    String nickname = (author != null) ? author.getNickname() : "알 수 없음";
                    List<ProductImage> images = productImageRepository.findByProductId(product.getId());
                    return ProductResponseDto.of(product, nickname, images);
                })
                .collect(Collectors.toList());
    }

    // 페이징 방식 상품 조회 ( 몇 개씩, 몇 페이지, 정렬 방법 , 검색 키워드, 카테고리 ID, 최소가격, 최대가격 )
    public Map<String, Object> findPaginated(int page, int size, String sort, String keyword, Long categoryId, Integer minPrice, Integer maxPrice) {


        // 페이지 번호와 사이즈를 이용해 DB에서 가져올 시작 지점(offset) 계산
        int offset = (page - 1) * size;
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("size", size);
        params.put("sort", sort);
        params.put("keyword", keyword);
        params.put("minPrice", minPrice);
        params.put("maxPrice", maxPrice);

        // 카테고리 필터링: 선택된 카테고리 및 하위 카테고리 포함
        if (categoryId != null) {
            List<Long> categoryIds = productCategoryService.getCategoryAndChildIds(categoryId);
            params.put("categoryIds", categoryIds);
        }

        List<Product> products = productRepository.findWithPaging(params);

        // 조회된 상품들의 작성자 정보(닉네임 등)를 가져오기 위해 ID 수집
        Set<Long> userIds =
                products.stream().map(Product::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = findUsersMapByIds(userIds);

        // 상품, 작성자, 이미지 조회 후 조합하여, List ProductResponseDto 생성
        List<ProductResponseDto> productDtos = products.stream()
                .map(product -> {
                    User author = userMap.get(product.getUserId());
                    String nickname = (author != null) ? author.getNickname() : "알 수 없음";
                    List<ProductImage> images = productImageRepository.findByProductId(product.getId());
                    return ProductResponseDto.of(product, nickname, images);
                })
                .collect(Collectors.toList());


        // HTML 최대 페이지 표시를 위한 totalPage 생성
        long totalProducts = productRepository.countAll(params);
        int totalPages = (int) Math.ceil((double) totalProducts / size);

        // productResponseDto, currentPage, totalPages 정보를 담은 MAP 생성
        Map<String, Object> response = new HashMap<>();
        response.put("products", productDtos);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);

        return response;
    }


    // ID로 특정 상품을 조회합니다. (이미지 포함)
    @Transactional
    public ProductResponseDto findProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        UserResponseDto userDto = userService.findUserById(product.getUserId());
        List<ProductImage> images = productImageRepository.findByProductId(id);
        return ProductResponseDto.of(product, userDto.getNickname(), images);
    }

    // 세션을 이용해 중복 조회 방지 및 조회수 증가
    @Transactional
    public void checkIncreaseView(Long productId, HttpSession session) {
        @SuppressWarnings("unchecked")
        Set<Long> visitedProductIds = (Set<Long>) session.getAttribute("visitedProductIds");

        if (visitedProductIds == null) {
            visitedProductIds = new HashSet<>();
        }

        if (!visitedProductIds.contains(productId)) {
            productRepository.increaseViews(productId);
            visitedProductIds.add(productId);
            session.setAttribute("visitedProductIds", visitedProductIds);
        }
    }


     //상품의 조회수를 1 증가시킵니다.
    @Transactional
    public void increaseViews(Long id) {
        productRepository.increaseViews(id);
    }


    //상품의 찜하기 수를 1 증가시킵니다.
    @Transactional
    public void increaseWishlistCount(Long id) {
        productRepository.increaseWishlistCount(id);
    }


    // 상품, 이미지 수정.
    @Transactional
    public void updateProduct(Long id, ProductUpdateRequestDto requestDto) throws IOException {
        // 기존 이미지 파일과 DB 기록을 삭제.
        List<ProductImage> oldImages = productImageRepository.findByProductId(id);
        for (ProductImage image : oldImages) {
            fileStore.deleteFile(image.getStoredFileName());
        }
        productImageRepository.deleteByProductId(id);

        // 새 이미지 파일 저장.
        List<ProductImage> storedImages = fileStore.storeFiles(requestDto.getImageFiles(), id, requestDto.getCategoryId());
        if (!storedImages.isEmpty()) {
            productImageRepository.saveAll(storedImages);
        }

        // 상품 수정.
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        product.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getPrice(), requestDto.getDealRegion(), requestDto.getCategoryId());
        productRepository.update(product);
    }

    //상품 삭제 (Soft Delete)
    @Transactional
    public void deleteProduct(Long id) {
        // Soft Delete이므로 이미지와 DB 기록은 유지하고 상태만 변경합니다.
        productRepository.deleteById(id);
    }

    // 삭제된 상품 목록 조회 (관리자용)
    public List<ProductResponseDto> findDeletedProducts() {
        List<Product> products = productRepository.findAllDeleted();
        Set<Long> userIds = products.stream().map(Product::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = findUsersMapByIds(userIds);

        return products.stream()
                .map(product -> {
                    User author = userMap.get(product.getUserId());
                    String nickname = (author != null) ? author.getNickname() : "알 수 없음";
                    List<ProductImage> images = productImageRepository.findByProductId(product.getId());
                    return ProductResponseDto.of(product, nickname, images);
                })
                .collect(Collectors.toList());
    }


    //사용자 ID 목록으로 사용자 정보를 조회합니다.
    private Map<Long, User> findUsersMapByIds(Set<Long> userIds) {
        return userService.findUserMapByIds(userIds);
    }


}