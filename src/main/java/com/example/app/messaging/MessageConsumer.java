package com.example.app.messaging;

import com.example.app.dto.MessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    private final ObjectMapper objectMapper;

    public MessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Process the message received from the queue and pass it to the counter service
     */
    @RabbitListener(queues = {"${rabbitmq.queue}"})
    public void processMessage(String rawMessage) {
        try {
            LOGGER.debug("Received message: {}", rawMessage);
            MessageDTO messageDto = objectMapper.readValue(rawMessage, MessageDTO.class);

            LOGGER.info("Message processed: {}", messageDto);
        } catch (JsonProcessingException exception) {
            LOGGER.error("Unable to process message: {} | Error: {} ", rawMessage, exception.getMessage());
            throw new RuntimeException(rawMessage, exception);
        }
    }
}