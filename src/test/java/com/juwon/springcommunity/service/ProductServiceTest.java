package com.juwon.springcommunity.service;

import com.juwon.springcommunity.domain.Product;
import com.juwon.springcommunity.domain.ProductCategory;
import com.juwon.springcommunity.domain.User;
import com.juwon.springcommunity.dto.ProductResponseDto;
import com.juwon.springcommunity.dto.UserResponseDto;
import com.juwon.springcommunity.repository.ProductImageRepository;
import com.juwon.springcommunity.repository.ProductRepository;
import com.juwon.springcommunity.util.FileStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private UserService userService;

    @Mock
    private FileStore fileStore;

    @Test
    @DisplayName("상품 ID로 조회 성공 테스트")
    void findProductById_Success() {
        // given
        Long productId = 1L;
        Long userId = 10L;

        Product product = new Product();
        product.setId(productId);
        product.setUserId(userId);
        product.setTitle("테스트 상품");
        product.setPrice(10000);
        
        ProductCategory category = new ProductCategory();
        category.setId(1L);
        category.setName("일반");
        product.setCategory(category);
        
        product.setCreatedAt(LocalDateTime.now());

        User user = new User();
        user.setId(userId);
        user.setNickname("판매자닉네임");
        UserResponseDto userDto = new UserResponseDto(user);

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(userService.findUserById(userId)).willReturn(userDto);
        given(productImageRepository.findByProductId(productId)).willReturn(new ArrayList<>());

        // when
        ProductResponseDto result = productService.findProductById(productId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("테스트 상품");
        assertThat(result.getAuthor()).isEqualTo("판매자닉네임");
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID 조회 시 예외 발생")
    void findProductById_NotFound() {
        // given
        Long invalidId = 999L;
        given(productRepository.findById(invalidId)).willReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> productService.findProductById(invalidId));
    }
}
