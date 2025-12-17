package com.juwon.springcommunity.controller;

import com.juwon.springcommunity.domain.ChatMessage;
import com.juwon.springcommunity.domain.ChatRoom;
import com.juwon.springcommunity.domain.Notification;
import com.juwon.springcommunity.domain.User;
import com.juwon.springcommunity.repository.UserRepository;
import com.juwon.springcommunity.service.ChatMessageService;
import com.juwon.springcommunity.service.ChatRoomService;
import com.juwon.springcommunity.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    public void message(ChatMessage message) {
        message.setCreatedAt(LocalDateTime.now());
        chatMessageService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/chat/room/" + message.getRoomId(), message);


        String receiver = chatRoomService.findReceiver(message.getRoomId(), message.getSender());
        notificationService.notifyChatRoomMessage(message, receiver);

        // notificationService 를 호출해서 message 를 전송
        // 로그인, 비로그인 구분 로직 + chatRoom URL 연결되어 있는가?
        // 구분해서 메세지 type 에 따른 전송을 이루어지게 만들어야함.
    }

    @GetMapping("/chat/room/{productId}")
    // 방을 생성함.
    public String createChatRoom(@PathVariable Long productId, @AuthenticationPrincipal UserDetails currentUser) {
        User buyer = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Long chatRoomId = chatRoomService.getOrCreateChatRoom(productId, buyer.getId());

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
    public String chatRoomList(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        List<ChatRoom> chatRooms = chatRoomService.findAllRoomsByUserId(user.getId());
        model.addAttribute("chatRooms", chatRooms);
        return "chatRoomList";
    }
}
