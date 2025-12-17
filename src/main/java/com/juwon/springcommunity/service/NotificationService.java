package com.juwon.springcommunity.service;

import com.juwon.springcommunity.domain.ChatMessage;
import com.juwon.springcommunity.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 채팅 메시지가 왔을 때, 수신자에게 실시간 알림을 보냅니다.
     * @param message 수신된 채팅 메시지
     * @param receiver 메시지 수신자의 username
     */
    public void notifyChatRoomMessage(ChatMessage message, String receiver) {
        // 메시지 발신자와 수신자가 다를 경우에만 알림 전송
        if (!message.getSender().equals(receiver)) {
            NotificationDto notificationDto = new NotificationDto(
                    message.getSender(),
                    "새로운 메시지: " + message.getMessage(),
                    message.getRoomId(),
                    "/chat/room/" + message.getRoomId()
            );

            // 사용자의 개인 큐로 알림을 전송
            simpMessagingTemplate.convertAndSendToUser(
                    receiver,
                    "/queue/notifications",
                    notificationDto
            );
        }
    }
}
