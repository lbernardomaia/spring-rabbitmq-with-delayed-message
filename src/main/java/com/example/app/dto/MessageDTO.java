package com.example.app.dto;

public record MessageDTO(
        String id,
        String message) {

    public MessageDTO {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
    }
}