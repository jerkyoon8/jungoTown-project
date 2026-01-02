package com.juwon.springcommunity.controller;

import com.juwon.springcommunity.domain.CarouselItem;
import com.juwon.springcommunity.dto.CarouselItemFormDto;
import com.juwon.springcommunity.service.CarouselService;
import com.juwon.springcommunity.util.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/carousel")
@RequiredArgsConstructor
public class AdminCarouselController {

    private final CarouselService carouselService;
    private final FileStore fileStore;

    @GetMapping
    public String list(Model model) {
        List<CarouselItem> items = carouselService.findAll();
        model.addAttribute("items", items);
        return "admin/carousel/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new CarouselItemFormDto());
        return "admin/carousel/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("form") CarouselItemFormDto form, RedirectAttributes redirectAttributes) throws IOException {
        CarouselItem item = new CarouselItem();
        item.setTitle(form.getTitle());
        item.setDescription(form.getDescription());
        item.setLinkUrl(form.getLinkUrl());
        item.setSortOrder(form.getSortOrder());
        item.setIsActive(form.getIsActive());

        MultipartFile imageFile = form.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            String storeFileName = fileStore.storeCarouselFile(imageFile);
            item.setImageUrl("/carousel-images/" + storeFileName);
        } else {
            // 이미지가 필수라면 에러 처리 필요하지만, 여기서는 일단 진행
            // 실제 서비스에선 validation 필요
        }

        carouselService.save(item);
        redirectAttributes.addFlashAttribute("message", "캐러셀 아이템이 등록되었습니다.");
        return "redirect:/admin/carousel";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        CarouselItem item = carouselService.findById(id);
        if (item == null) {
            return "redirect:/admin/carousel";
        }

        CarouselItemFormDto form = new CarouselItemFormDto();
        form.setId(item.getId());
        form.setTitle(item.getTitle());
        form.setDescription(item.getDescription());
        form.setLinkUrl(item.getLinkUrl());
        form.setSortOrder(item.getSortOrder());
        form.setIsActive(item.getIsActive());
        form.setSavedImageUrl(item.getImageUrl());

        model.addAttribute("form", form);
        return "admin/carousel/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @ModelAttribute("form") CarouselItemFormDto form, RedirectAttributes redirectAttributes) throws IOException {
        CarouselItem item = carouselService.findById(id);
        if (item == null) {
            return "redirect:/admin/carousel";
        }

        item.setTitle(form.getTitle());
        item.setDescription(form.getDescription());
        item.setLinkUrl(form.getLinkUrl());
        item.setSortOrder(form.getSortOrder());
        item.setIsActive(form.getIsActive());

        MultipartFile imageFile = form.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            // 기존 파일 삭제 로직이 있으면 좋음 (생략)
            String storeFileName = fileStore.storeCarouselFile(imageFile);
            item.setImageUrl("/carousel-images/" + storeFileName);
        }

        carouselService.update(item);
        redirectAttributes.addFlashAttribute("message", "캐러셀 아이템이 수정되었습니다.");
        return "redirect:/admin/carousel";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // 기존 파일 삭제 로직 추가 가능 (생략)
        carouselService.delete(id);
        redirectAttributes.addFlashAttribute("message", "캐러셀 아이템이 삭제되었습니다.");
        return "redirect:/admin/carousel";
    }
}
