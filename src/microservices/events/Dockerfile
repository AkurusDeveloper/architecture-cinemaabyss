# Этап сборки
FROM maven:3.8-openjdk-21 AS build
WORKDIR /app
COPY pom.xml .
# Загружаем зависимости отдельно для лучшего кэширования
RUN mvn dependency:go-offline

# Копируем исходный код и собираем проект
COPY src ./src
RUN mvn clean package

# Финальный этап - создание легковесного образа
FROM openjdk:21-slim

WORKDIR /app

# Копируем собранный JAR из этапа сборки
COPY --from=build /app/target/events-service-1.0.0-jar-with-dependencies.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]