FROM gradle:8.4-jdk21 AS builder

WORKDIR /app

COPY build.gradle settings.gradle ./
RUN gradle --no-daemon dependencies

COPY . .

RUN gradle --no-daemon clean build -x test

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENV SPRING_APPLICATION_NAME=SciTechNewsService
ENV SERVER_PORT=8083

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]