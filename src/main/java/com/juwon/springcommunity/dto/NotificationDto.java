package com.juwon.springcommunity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {


    private String sender;
    private String messageContent;
    private Long chatRoomId;

    // 토스트 메세지 박스 클릭시 이동할 URL
    private String redirectUrl;
}