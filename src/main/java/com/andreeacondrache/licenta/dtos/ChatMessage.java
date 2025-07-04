package com.andreeacondrache.licenta.dtos;


import lombok.Data;

@Data
public class ChatMessage {
    private String content;
    private Long userId;
}