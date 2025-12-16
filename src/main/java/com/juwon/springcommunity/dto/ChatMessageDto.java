package com.juwon.springcommunity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDto {
    private String sender;
    private String receiver;
    private String content;
}
