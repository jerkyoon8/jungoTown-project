package com.juwon.springcommunity.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private Long id;
    private Long roomId;
    private String sender;    // 닉네임 (DB 컬럼: sender)
    private Long senderId;    // 사용자 ID (DB 컬럼: sender_id)
    private String message;
    private LocalDateTime createdAt;
}
