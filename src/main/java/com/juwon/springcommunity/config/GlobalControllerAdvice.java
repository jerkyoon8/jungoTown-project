package com.juwon.springcommunity.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    /**
     * 모든 뷰에 공통 모델 속성을 추가합니다.
     * 현재 요청 경로가 "/chat"으로 시작하는지 여부를 확인하여,
     * 알림을 숨겨야 하는지 결정하는 'hideNotifications' 플래그를 모델에 추가합니다.
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
}
