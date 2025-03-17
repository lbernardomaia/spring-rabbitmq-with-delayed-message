# Spring RabbitMQ with Delayed Message

This is a Dockerized Spring Boot project that demonstrates the use of 
RabbitMQ with delayed message processing RabbitMQ plugin ([RabbitMQ Delayed Message Exchange](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange)) 
and how retry mechanism can be implemented.

## Prerequisites

- Docker
- Docker Compose
- Java 17
- Gradle

## Building the Project

To build the project, run the following command:

```sh
./gradlew build
```

## Running the Project

To run the project using Docker Compose, execute:

```sh
docker-compose up --build
```

This will start the Spring Boot application and RabbitMQ with the delayed message exchange plugin.

Check the console output for the application logs to see the messages being processed.

## Accessing RabbitMQ Management

RabbitMQ Management can be accessed at [http://localhost:15672](http://localhost:15672) with the default credentials (guest/guest).