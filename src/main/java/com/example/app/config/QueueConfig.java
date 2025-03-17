package com.example.app.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {
    // Exchanges
    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.deadLetter.exchange}")
    private String deadLetterExchange;

    @Value("${rabbitmq.requeue.delay.exchange}")
    String delayExchangeName;

    // Routing keys
    @Value("${rabbitmq.routingKey}")
    private String routingKey;

    // Queue names
    @Value("${rabbitmq.queue}")
    String queueName;

    @Value("${rabbitmq.deadLetter.queue}")
    String deadLetterQueueName;

    @Value("${rabbitmq.parking_lot.queue}")
    String parkingLotQueueName;

    public static final String X_RETRIES_HEADER = "x-retries";

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    FanoutExchange deadLetterExchange() {
        return new FanoutExchange(deadLetterExchange);
    }

    @Bean
    Queue queue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", deadLetterExchange)
                .build();
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(deadLetterQueueName)
                .build();
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(queue())
                .to(topicExchange())
                .with(routingKey);
    }

    @Bean
    Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange());
    }


    @Bean
    public Queue parkingLot() {
        return new Queue(parkingLotQueueName);
    }

    @Bean
    public DirectExchange delayExchange() {
        DirectExchange exchange = new DirectExchange(delayExchangeName);
        exchange.setDelayed(true);
        return exchange;
    }

    @Bean
    public Binding bindOriginalToDelay() {
        return BindingBuilder.bind(new Queue(queueName)).to(delayExchange()).with(queueName);
    }
}

