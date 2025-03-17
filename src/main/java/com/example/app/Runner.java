package com.example.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for sending a valid message to the queue and waiting for 10 seconds before sending an invalid message
 */
@Component
public class Runner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);

    @Value("${rabbitmq.exchange}")
    String customerDataExchange;

    @Value("${rabbitmq.routingKey}")
    String customerDataRoutingKey;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public Runner( RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws InterruptedException, JsonProcessingException {
        Map<String, Object> validMessage = new HashMap<>();
        UUID randomUUID = UUID.randomUUID();
        validMessage.put("id", randomUUID);
        validMessage.put("message", "Random message for UUID: " + randomUUID);

        LOGGER.info("Sending valid message: {}", validMessage);

        rabbitTemplate.convertAndSend(
                customerDataExchange,
                customerDataRoutingKey,
                objectMapper.writeValueAsString(validMessage)
        );

        LOGGER.info("Waiting for 15 seconds before sending an invalid message");
        Thread.sleep(TimeUnit.SECONDS.toMillis(15));

        LOGGER.info("Sending invalid message");

        Map<String, Object> invalidMessage = new HashMap<>();
        validMessage.put("id", UUID.randomUUID());

        rabbitTemplate.convertAndSend(
                customerDataExchange,
                customerDataRoutingKey,
                objectMapper.writeValueAsString(invalidMessage)
        );
    }
}
