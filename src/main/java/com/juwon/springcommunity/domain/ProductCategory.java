package com.juwon.springcommunity.domain;


public enum ProductCategory {
    GENERAL("일반"),
    INFORMATION("정보"),
    NOTICE("공지");

    private final String description;

    ProductCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}