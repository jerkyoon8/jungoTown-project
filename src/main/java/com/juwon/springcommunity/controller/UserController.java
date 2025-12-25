package com.juwon.springcommunity.controller;

import com.juwon.springcommunity.dto.UserCreateRequestDto;
import com.juwon.springcommunity.dto.UserResponseDto;
import com.juwon.springcommunity.dto.UserUpdateRequestDto;
import com.juwon.springcommunity.security.oauth.SessionUser;
import com.juwon.springcommunity.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 닉네임 중복 확인
    @GetMapping("/users/check-nickname")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkNicknameDuplicate(@RequestParam String nickname) {
        boolean isDuplicate = userService.isNicknameDuplicate(nickname);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicate", isDuplicate);
        return ResponseEntity.ok(response);
    }

    // 사용자 생성 폼을 보여준다
    @GetMapping("/users/new")
    public String createUserForm(Model model) {
        model.addAttribute("userCreateRequestDto", new UserCreateRequestDto());
        return "users/createUserForm";
    }

    // 사용자 생성을 처리한다
    @PostMapping("/users")
    public String createUser(@Validated @ModelAttribute("userCreateRequestDto") UserCreateRequestDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/createUserForm";
        }

        try {
            userService.createUser(dto);
        } catch (IllegalArgumentException e) {
            // 이메일 중복 또는 비밀번호 불일치 등 에러 처리
            bindingResult.reject("signupFailed", e.getMessage());
            return "users/createUserForm";
        }

        return "redirect:/";
    }

    // 전체 사용자 목록을 보여준다
    @GetMapping("/users")
    public String getAllUsers(Model model) {
        List<UserResponseDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users/userList";
    }

    // 특정 사용자 상세 정보를 보여준다
    @GetMapping("/users/{userId}")
    public String getUserById(@PathVariable Long userId, Model model,
                              @SessionAttribute(name = "user", required = false) SessionUser sessionUser) {
        UserResponseDto user = userService.findUserById(userId);
        model.addAttribute("user", user);

        boolean isAuthorized = userService.isAuthorized(user.getEmail(), sessionUser);
        model.addAttribute("isAuthorized", isAuthorized);

        return "users/userDetail";
    }



    // 사용자 정보 수정 폼을 보여준다
    @GetMapping("/users/{userId}/edit")
    public String updateUserForm(@PathVariable Long userId, Model model) {
        UserResponseDto userResponseDto = userService.findUserById(userId);
        
        // 응답 DTO의 정보로 수정 DTO를 만들어 모델에 추가
        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto();
        userUpdateRequestDto.setNickname(userResponseDto.getNickname());

        model.addAttribute("userUpdateRequestDto", userUpdateRequestDto);
        model.addAttribute("userId", userId);
        return "users/updateUserForm";
    }

    // 사용자 정보 수정을 처리한다
    @PostMapping("/users/{userId}/update")
    public String updateUser(@PathVariable Long userId, 
                             @Validated @ModelAttribute("userUpdateRequestDto") UserUpdateRequestDto dto,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/updateUserForm";
        }
        userService.updateUser(userId, dto);
        return "redirect:/users/" + userId;
    }

    // 사용자 삭제 (본인 또는 관리자)
    @PostMapping("/users/{userId}/delete")
    public String deleteUser(@PathVariable Long userId, Principal principal) {
        String email = principal.getName();
        if (principal instanceof OAuth2AuthenticationToken) {
            email = ((OAuth2AuthenticationToken) principal).getPrincipal().getAttribute("email");
        }
        com.juwon.springcommunity.domain.User user = userService.findUserByEmail(email);

        if (!user.getId().equals(userId) && !user.getRole().getKey().equals("ROLE_ADMIN")) {
            throw new IllegalStateException("권한이 없습니다.");
        }

        userService.deleteUser(userId);

        if (user.getRole().getKey().equals("ROLE_ADMIN")) {
            return "redirect:/admin/users";
        }
        return "redirect:/";
    }

    // 로그인 Form
    @GetMapping("/users/login")
    public String loginForm(){
        return "users/loginForm";
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String myPage(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/users/login";
        }
        String email = principal.getName();
        if (principal instanceof OAuth2AuthenticationToken) {
            email = ((OAuth2AuthenticationToken) principal).getPrincipal().getAttribute("email");
        }
        UserResponseDto user = userService.findUserDtoByEmail(email);
        model.addAttribute("user", user);

        return "users/myPage";
    }
}
