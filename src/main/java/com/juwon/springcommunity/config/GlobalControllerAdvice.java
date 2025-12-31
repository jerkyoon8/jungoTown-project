package com.juwon.springcommunity.config;

import com.juwon.springcommunity.domain.ProductCategory;
import com.juwon.springcommunity.service.ProductCategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final ProductCategoryService productCategoryService;

    /**
     * 모든 뷰에 공통적으로 카테고리 목록을 제공합니다.
     * 헤더의 드롭다운 메뉴 등에서 사용됩니다.
     */
    @ModelAttribute("headerCategories")
    public List<ProductCategory> headerCategories() {
        return productCategoryService.getAllCategories();
    }

    /**
     * 모든 뷰에 공통 모델 속성을 추가합니다。
     * 현재 요청 경로가 "/chat"으로 시작하는지 여부를 확인하여,
     * 알림을 숨겨야 하는지 결정하는 'hideNotifications' 플래그를 모델에 추가합니다。
     * @param request 현재 HTTP 요청
     * @return 알림을 숨겨야 하면 true, 그렇지 않으면 false
     */
    @ModelAttribute("hideNotifications")
    public boolean hideNotifications(HttpServletRequest request) {
        if (request == null || request.getServletPath() == null) {
            return false; // 기본적으로 알림을 숨기지 않음
        }
        String path = request.getServletPath();
        boolean shouldHide = path.startsWith("/chat");
        return shouldHide;
    }

    /**
     * 처리되지 않은 모든 예외를 처리하는 핸들러입니다.
     * 예외 발생 시 로그를 남기고, 사용자에게는 500 에러 페이지를 보여줍니다.
     * @param e 발생한 예외
     * @param request 예외가 발생한 요청
     * @return 에러 정보와 함께 보여줄 ModelAndView 객체
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e, HttpServletRequest request) {
        log.error("Exception occurred: {} at {}", e.getMessage(), request.getRequestURI(), e);

        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("url", request.getRequestURL());
        mav.setViewName("error/500");
        return mav;
    }
}
