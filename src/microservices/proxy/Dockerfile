FROM maven:3.8-openjdk-21 AS build
WORKDIR /app
COPY pom.xml .
# Загружаем зависимости отдельно для лучшего кэширования
RUN mvn dependency:go-offline

# Копируем исходный код и собираем проект
COPY src ./src
RUN mvn clean package

FROM openjdk:21-slim

WORKDIR /app

COPY --from=build /app/target/proxy-service-1.0.0.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]