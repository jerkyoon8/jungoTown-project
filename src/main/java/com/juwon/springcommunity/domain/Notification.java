package com.juwon.springcommunity.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    private String receiver;
    private String sender;
    private String content;
    private String type;
}
