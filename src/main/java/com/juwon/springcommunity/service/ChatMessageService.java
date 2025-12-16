package com.juwon.springcommunity.service;

import com.juwon.springcommunity.domain.ChatMessage;
import com.juwon.springcommunity.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private static final int MESSAGES_PER_PAGE = 30;


    // 메시지를 저장하는 비즈니스 로직을 수행합니다.
    @Transactional
    public void saveMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

    // 특정 채팅방의 메시지 목록을 페이지 단위로 조회하는 비즈니스 로직을 수행합니다.
    public List<ChatMessage> getMessages(Long roomId, Long lastMessageId) {
        List<ChatMessage> messages;
        if (lastMessageId == null) {
            // 초기 로딩: 가장 최근 메시지 30개를 가져옴
            messages = chatMessageRepository.findInitialMessages(roomId, MESSAGES_PER_PAGE);
        } else {
            // 이전 메시지 로딩: lastMessageId 이전의 메시지 30개를 가져옴
            messages = chatMessageRepository.findMessagesWithPaging(roomId, lastMessageId, MESSAGES_PER_PAGE);
        }
        return messages;
    }
}
