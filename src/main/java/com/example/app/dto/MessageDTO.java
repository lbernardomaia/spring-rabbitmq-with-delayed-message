package com.example.app.dto;

public record MessageDTO(
        String id,
        String message) {

    public MessageDTO {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
    }
}