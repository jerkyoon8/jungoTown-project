package com.juwon.springcommunity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // index 페이지를 연결
    @GetMapping("/")
    public String home() {
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
