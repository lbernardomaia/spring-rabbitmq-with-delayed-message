package com.example.app.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.app.config.QueueConfig.X_RETRIES_HEADER;

@Component
public class MessageProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);
    private static final int NUMBER_OF_RETRIES = 3;
    private static final int DELAY_IN_SECONDS= 15;

    private final RabbitTemplate rabbitTemplate;
    private final String queueName;
    private final String parkingLotQueueName;
    private final String delayExchangeName;

    public MessageProducer(RabbitTemplate rabbitTemplate,
                           @Value("${rabbitmq.queue}") String queueName,
                           @Value("${rabbitmq.parking_lot.queue}") String parkingLotQueueName,
                           @Value("${rabbitmq.requeue.delay.exchange}") String delayExchangeName){
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
        this.parkingLotQueueName = parkingLotQueueName;
        this.delayExchangeName = delayExchangeName;
    }

    /**
     * Re-publish the failed message to the original queue with a delay
     * If the message has been retried more than the maximum number of retries, send it to the parking lot queue
     */
    @RabbitListener(queues = {"${rabbitmq.deadLetter.queue}"})
    public void rePublish(Message failedMessage) {
        Map<String, Object> headers = failedMessage.getMessageProperties().getHeaders();
        Integer retriesHeader = (Integer) headers.getOrDefault(X_RETRIES_HEADER, 0);
        if (retriesHeader < NUMBER_OF_RETRIES) {
            headers.put(X_RETRIES_HEADER, retriesHeader + 1);
            headers.put("x-delay", TimeUnit.SECONDS.toMillis(DELAY_IN_SECONDS));
            LOGGER.warn("Re-publishing message to the original queue: {} with a delay of {} seconds", failedMessage, DELAY_IN_SECONDS);
            this.rabbitTemplate.send(delayExchangeName, queueName, failedMessage);
        } else {
            LOGGER.error("Sending message to the parking lot: {} after {} retries", failedMessage, NUMBER_OF_RETRIES);
            this.rabbitTemplate.send(parkingLotQueueName, failedMessage);
        }
    }
}