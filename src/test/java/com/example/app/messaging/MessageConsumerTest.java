package com.example.app.messaging;

import com.example.app.dto.MessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageConsumerTest {

    private MessageConsumer messageConsumer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = mock(ObjectMapper.class);
        messageConsumer = new MessageConsumer(objectMapper);
    }

    @Test
    void processValidMessage() throws JsonProcessingException {
        String rawMessage = "{\"id\":\"1\",\"content\":\"Test message\"}";
        MessageDTO messageDTO = new MessageDTO("1", "Test message");

        when(objectMapper.readValue(rawMessage, MessageDTO.class)).thenReturn(messageDTO);

        messageConsumer.processMessage(rawMessage);
    }

    @Test
    void processInvalidJsonMessage() throws JsonProcessingException {
        String rawMessage = "invalid json";

        when(objectMapper.readValue(anyString(), eq(MessageDTO.class))).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> messageConsumer.processMessage(rawMessage));
    }

    @Test
    void processMessageWithIllegalArgumentException() throws JsonProcessingException {
        String rawMessage = "{\"id\":1,\"content\":\"Test message\"}";

        when(objectMapper.readValue(anyString(), eq(MessageDTO.class))).thenThrow(IllegalArgumentException.class);

        assertThrows(RuntimeException.class, () -> messageConsumer.processMessage(rawMessage));
    }
}