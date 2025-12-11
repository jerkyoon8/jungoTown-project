package com.juwon.springcommunity.controller;

import com.juwon.springcommunity.dto.UserCreateRequestDto;
import com.juwon.springcommunity.dto.UserResponseDto;
import com.juwon.springcommunity.dto.UserUpdateRequestDto;
import com.juwon.springcommunity.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 사용자 ID 중복 확인
    @GetMapping("/check-username")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkUsernameDuplicate(@RequestParam String username) {

        boolean isDuplicate = userService.isUsernameDuplicate(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicate", isDuplicate);

        return ResponseEntity.ok(response);
    }

    // 닉네임 중복 확인
    @GetMapping("/check-nickname")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkNicknameDuplicate(@RequestParam String nickname) {
        boolean isDuplicate = userService.isNicknameDuplicate(nickname);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicate", isDuplicate);
        return ResponseEntity.ok(response);
    }

    // 사용자 생성 폼을 보여준다
    @GetMapping("/new")
    public String createUserForm(Model model) {
        model.addAttribute("userCreateRequestDto", new UserCreateRequestDto());
        return "users/createUserForm";
    }

    // 사용자 생성을 처리한다
    @PostMapping
    public String createUser(@Validated @ModelAttribute("userCreateRequestDto") UserCreateRequestDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/createUserForm";
        }


        // 비밀번호와 비밀번호 확인이 일치하는지 검사
        try {
            userService.createUser(dto);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("passwordConfirm", "password.mismatch", e.getMessage());
            return "users/createUserForm";
        }

        return "redirect:/users";
    }

    // 전체 사용자 목록을 보여준다
    @GetMapping
    public String getAllUsers(Model model) {
        List<UserResponseDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users/userList";
    }

    // 특정 사용자 상세 정보를 보여준다
    @GetMapping("/{userId}")
    public String getUserById(@PathVariable Long userId, Model model,
                              @AuthenticationPrincipal UserDetails currentUser) {
        UserResponseDto user = userService.findUserById(userId);
        model.addAttribute("user", user);

        boolean isAuthorized = userService.isAuthorized(user.getUsername(), currentUser);
        model.addAttribute("isAuthorized", isAuthorized);

        return "users/userDetail";
    }

    // 사용자 정보 수정 폼을 보여준다
    @GetMapping("/{userId}/edit")
    public String updateUserForm(@PathVariable Long userId, Model model) {
        UserResponseDto userResponseDto = userService.findUserById(userId);
        
        // 응답 DTO의 정보로 수정 DTO를 만들어 모델에 추가
        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto();
        userUpdateRequestDto.setUsername(userResponseDto.getUsername());
        userUpdateRequestDto.setEmail(userResponseDto.getEmail());

        model.addAttribute("userUpdateRequestDto", userUpdateRequestDto);
        model.addAttribute("userId", userId);
        return "users/updateUserForm";
    }

    // 사용자 정보 수정을 처리한다
    @PostMapping("/{userId}/update")
    public String updateUser(@PathVariable Long userId, 
                             @Validated @ModelAttribute("userUpdateRequestDto") UserUpdateRequestDto dto,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/updateUserForm";
        }
        userService.updateUser(userId, dto);
        return "redirect:/users/" + userId;
    }

    // 사용자를 삭제한다
    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "redirect:/users";
    }

    // 로그인 Form
    @GetMapping("/login")
    public String loginForm(){
        return "users/loginForm";
    }
}
