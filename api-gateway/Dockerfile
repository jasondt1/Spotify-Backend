FROM gradle:8.7.0-jdk17 AS builder
WORKDIR /app

COPY build.gradle ./
COPY src ./src

RUN gradle clean build -x test --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
