package com.example.app.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static com.example.app.config.QueueConfig.X_RETRIES_HEADER;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageProducerTest {

    private MessageProducer messageProducer;
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        messageProducer = new MessageProducer(
                rabbitTemplate,
                "testQueue",
                "parkingLotQueue",
                "delayExchange"
        );
    }

    @Test
    void rePublishMessageWithRetriesLeft() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader(X_RETRIES_HEADER, 1);
        Message message = new Message("test".getBytes(), messageProperties);

        messageProducer.rePublish(message);

        verify(rabbitTemplate).send(eq("delayExchange"), eq("testQueue"), any(Message.class));
    }

    @Test
    void rePublishMessageWithMaxRetries() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader(X_RETRIES_HEADER, 3);
        Message message = new Message("test".getBytes(), messageProperties);

        messageProducer.rePublish(message);

        verify(rabbitTemplate).send(eq("parkingLotQueue"), any(Message.class));
    }

    @Test
    void rePublishMessageWithoutRetriesHeader() {
        MessageProperties messageProperties = new MessageProperties();
        Message message = new Message("test".getBytes(), messageProperties);

        messageProducer.rePublish(message);

        verify(rabbitTemplate).send(eq("delayExchange"), eq("testQueue"), any(Message.class));
    }
}