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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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


        String receiver = chatRoomService.findReceiver(message.getRoomId(), user.getNickname()); 
        // findReceiver가 String(닉네임)을 반환한다고 가정. 
        // 만약 email을 반환한다면 로직 확인 필요. -> 기존 코드가 sender(String)를 썼으므로 닉네임일 확률 높음.
        
        notificationService.notifyChatRoomMessage(message, receiver);
    }

    @GetMapping("/chat/room/{productId}")
    // 방을 생성함.
    public String createChatRoom(@PathVariable Long productId, @SessionAttribute("user") SessionUser sessionUser) {
        Long chatRoomId = chatRoomService.getOrCreateChatRoom(productId, sessionUser.getId());

        return "redirect:/chat/" + chatRoomId;
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
}
