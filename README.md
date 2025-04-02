# Spring RabbitMQ with Delayed Message

This is a Dockerized Spring Boot project that demonstrates the use of 
RabbitMQ with delayed message processing RabbitMQ plugin ([RabbitMQ Delayed Message Exchange](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange)) 
and how retry mechanism can be implemented.

The project has a runner ([Runner.java](src/main/java/com/example/app/Runner.java)) 
that sends a valid message and an invalid message to the RabbitMQ queue.

The invalid message that cannot be processed will be retried by the [MessageProducer.java](src/main/java/com/example/app/messaging/MessageProducer.java) 3 times with a delay of 15 seconds 
and then sent to the parking lot queue when the retry limit is reached.

#### Messages example:

The id and message are mandatory fields in the message.

Valid message sent:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "message": "Random message for UUID: 550e8400-e29b-41d4-a716-446655440000"
}
```

Invalid message sent:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000"
}
```

## Prerequisites

- Docker
- Docker Compose
- Java 17
- Gradle

## Running the Project

To run the project using Docker Compose, execute:

```sh
docker-compose up --build
```

This will start the Spring Boot application and RabbitMQ with the delayed message exchange plugin.

Check the console output for the application logs to see the messages being processed.

## Accessing RabbitMQ Management

RabbitMQ Management can be accessed at [http://localhost:15672](http://localhost:15672) with the default credentials (guest/guest).