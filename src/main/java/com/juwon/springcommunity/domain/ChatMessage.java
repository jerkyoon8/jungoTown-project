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
    private String sender;
    private String message;
    private LocalDateTime createdAt;
}
