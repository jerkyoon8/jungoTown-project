package com.juwon.springcommunity.controller;

import com.juwon.springcommunity.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate; // SimpMessagingTemplate 주입

    // 클라이언트에서 메세지를 받아서 특정 사용자에게 알람 전송
    @MessageMapping("/chat/notification")
    public void handleClientNotification(Notification notification){
        // 클라이언트가 보낸 알림을 해당 수신자에게 다시 전송
        if (notification.getReceiver() != null && !notification.getReceiver().isEmpty()) {
            sendNotificationToUser(notification.getReceiver(), notification);
        }
    }

    // 특정 사용자에게 서버에서 알람 전송
    public void sendNotificationToUser(String username, Notification notification) {
        // /user/{username}/queue/notifications 경로로 알림 전송
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/notifications", // WebSocketConfig에서 설정한 /queue 접두사 사용
                notification
        );
    }
}
