package com.juwon.springcommunity.controller;

import com.juwon.springcommunity.dto.UserCreateRequestDto;
import com.juwon.springcommunity.dto.UserResponseDto;
import com.juwon.springcommunity.dto.UserUpdateRequestDto;
import com.juwon.springcommunity.security.oauth.SessionUser;
import com.juwon.springcommunity.dto.WishListResponseDto;
import com.juwon.springcommunity.service.ProductWishListService;
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
    private final ProductWishListService productWishListService;

    public UserController(UserService userService, ProductWishListService productWishListService) {
        this.userService = userService;
        this.productWishListService = productWishListService;
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

    // 사용자 생성을 처리한다 (AJAX/JSON 요청)
    @PostMapping("/users")
    @ResponseBody
    public ResponseEntity<?> createUser(@Validated @RequestBody UserCreateRequestDto dto, BindingResult bindingResult) {
        // 유효성 검사 실패 시
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            userService.createUser(dto);
        } catch (IllegalArgumentException e) {
            Map<String, String> errors = new HashMap<>();
            // 서비스에서 던지는 예외 메시지 분석 (이메일 중복 등)
            if (e.getMessage().contains("이메일")) {
                errors.put("email", e.getMessage());
            } else if (e.getMessage().contains("비밀번호")) {
                errors.put("passwordConfirm", e.getMessage());
            } else {
                errors.put("global", e.getMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        // 성공 시 리디렉션 URL 전달
        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", "/");
        return ResponseEntity.ok(response);
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

    // 찜한 상품 목록 페이지
    @GetMapping("/wishes")
    public String wishList(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/users/login";
        }
        String email = principal.getName();
        if (principal instanceof OAuth2AuthenticationToken) {
            email = ((OAuth2AuthenticationToken) principal).getPrincipal().getAttribute("email");
        }
        UserResponseDto user = userService.findUserDtoByEmail(email);
        List<WishListResponseDto> wishList = productWishListService.getWishList(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("wishList", wishList);

        return "users/wishlist";
    }
}
