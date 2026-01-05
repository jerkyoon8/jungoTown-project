package com.juwon.springcommunity.controller;

import com.juwon.springcommunity.domain.ChatMessage;
import com.juwon.springcommunity.domain.ChatRoom;
import com.juwon.springcommunity.domain.User;
import com.juwon.springcommunity.repository.UserRepository;
import com.juwon.springcommunity.security.oauth.SessionUser;
import com.juwon.springcommunity.service.ChatMessageService;
import com.juwon.springcommunity.service.ChatRoomService;
import com.juwon.springcommunity.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 메세지 전송
    @MessageMapping("/chat/message")
    public void message(ChatMessage message, Principal principal) {
        if (principal == null) {
            // 로그인 안된 사용자 처리 (예외 혹은 무시)
            return; 
        }
        
        String email = principal.getName();
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) principal;
            email = oauthToken.getPrincipal().getAttribute("email");
        }
        
        User user = userRepository.findByEmail(email)
                 .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        message.setSender(user.getNickname()); // DB 저장용 닉네임
        message.setSenderId(user.getId());     // DB 저장용 ID
        message.setCreatedAt(LocalDateTime.now());
        
        chatMessageService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/chat/room/" + message.getRoomId(), message);


        String receiver = chatRoomService.findReceiver(message.getRoomId(), user.getEmail());
        
        notificationService.notifyChatRoomMessage(message, receiver);
    }

    @GetMapping("/chat/room/{productId}")
    // 방을 생성함. (기존: 리다이렉트 방식 - 유지하되 잘 안 쓰임)
    public String createChatRoom(@PathVariable Long productId, @SessionAttribute("user") SessionUser sessionUser) {
        Long chatRoomId = chatRoomService.getOrCreateChatRoom(productId, sessionUser.getId());

        return "redirect:/chat/" + chatRoomId;
    }
    
    // 오버레이 채팅용: 방 생성 후 JSON 반환
    @PostMapping("/chat/room/{productId}/create-api")
    @ResponseBody
    public ChatRoom createChatRoomApi(@PathVariable Long productId, @SessionAttribute("user") SessionUser sessionUser) {
        Long chatRoomId = chatRoomService.getOrCreateChatRoom(productId, sessionUser.getId());
        return chatRoomService.findRoomById(chatRoomId);
    }

    @GetMapping("/chat/{roomId}")
    public String chatRoom(@PathVariable Long roomId, Model model) {
        ChatRoom chatRoom = chatRoomService.findRoomById(roomId);
        List<ChatMessage> messages = chatMessageService.getMessages(roomId, null);
        model.addAttribute("chatRoom", chatRoom);
        model.addAttribute("messages", messages);
        return "chat";
    }

    @GetMapping("/chat/rooms")
    public String chatRoomList(@SessionAttribute("user") SessionUser sessionUser, Model model) {
        List<ChatRoom> chatRooms = chatRoomService.findAllRoomsByUserId(sessionUser.getId());
        model.addAttribute("chatRooms", chatRooms);
        return "chatRoomList";
    }

    // --- API Endpoints for Overlay Chat ---

    @GetMapping("/api/chat/rooms")
    @ResponseBody
    public List<ChatRoom> apiChatRooms(@SessionAttribute(name = "user", required = false) SessionUser sessionUser) {
        if (sessionUser == null) return List.of();
        return chatRoomService.findAllRoomsByUserId(sessionUser.getId());
    }

    @GetMapping("/api/chat/messages/{roomId}")
    @ResponseBody
    public List<ChatMessage> apiChatMessages(@PathVariable Long roomId) {
        return chatMessageService.getMessages(roomId, null);
    }

    @GetMapping("/api/chat/room/{roomId}")
    @ResponseBody
    public ChatRoom apiChatRoom(@PathVariable Long roomId) {
        return chatRoomService.findRoomById(roomId);
    }

    /**
     * 채팅방 삭제 (방장/참여자 구분 없이 완전 삭제)
     */
    @PostMapping("/chat/room/{roomId}/delete")
    public String deleteChatRoom(@PathVariable Long roomId) {
        chatRoomService.deleteChatRoom(roomId);
        return "redirect:/chat/rooms";
    }

    @DeleteMapping("/api/chat/room/{roomId}")
    @ResponseBody
    public String apiDeleteChatRoom(@PathVariable Long roomId) {
        chatRoomService.deleteChatRoom(roomId);
        return "success";
    }
}
