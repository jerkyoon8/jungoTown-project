package com.juwon.springcommunity.controller;

import com.juwon.springcommunity.dto.UserResponseDto;
import com.juwon.springcommunity.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String adminHome() {
        return "admin/index";
    }

    @GetMapping("/users")
    public String userList(Model model) {
        List<UserResponseDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/userList";
    }
}