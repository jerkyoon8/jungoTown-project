package com.juwon.springcommunity.dto;

import com.juwon.springcommunity.domain.Product;
import com.juwon.springcommunity.domain.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateRequestDto {

    @NotBlank(message = "제목이 비어있습니다.")
    @Size(max = 255, message = "제목은 255자를 넘을 수 없습니다.")
    private String title;

    @NotBlank(message = "내용이 비어있습니다.")
    @Size(max = 2000, message = "내용은 2000자를 넘을 수 없습니다.")
    private String content;

    @NotNull(message = "가격을 입력해주세요.")
    private int price;

    @NotBlank(message = "거래 희망 지역을 입력해주세요.")
    private String dealRegion;

    @NotNull(message = "카테고리를 선택해주세요.")
    private ProductCategory category;

    private List<MultipartFile> imageFiles;

    public Product toEntity(Long userId) {
        Product product = new Product();
        product.setTitle(this.title);
        product.setContent(this.content);
        product.setPrice(this.price);
        product.setDealRegion(this.dealRegion);
        product.setCategory(this.category);
        product.setUserId(userId);
        return product;
    }
}
