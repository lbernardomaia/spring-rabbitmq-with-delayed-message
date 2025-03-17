FROM gradle:8-jdk17 AS builder
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src src/
RUN gradle build -x test

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
