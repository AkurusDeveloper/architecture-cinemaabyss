package com.cinemaabyss.events;

import com.cinemaabyss.events.api.Server;
import com.cinemaabyss.events.api.handlers.EventsHandler;
import com.cinemaabyss.events.api.handlers.HealthHandler;
import com.cinemaabyss.events.config.Config;
import com.cinemaabyss.events.kafka.EventProcessor;
import com.cinemaabyss.events.kafka.EventConsumer;
import com.cinemaabyss.events.model.MovieEvent;
import com.cinemaabyss.events.model.PaymentEvent;
import com.cinemaabyss.events.model.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class EventsServiceMain {
    private static final Logger logger = LoggerFactory.getLogger(EventsServiceMain.class);

    public static void main(String[] args) {
        try {
            Config config = new Config();

            // Создание обработчиков и сервера
            EventsHandler eventsHandler = new EventsHandler(config.getKafkaBrokers());
            HealthHandler healthHandler = new HealthHandler();
            Server server = new Server(config, eventsHandler, healthHandler);

            // Создание процессора событий
            EventProcessor eventProcessor = new EventProcessor(config.getKafkaBrokers());

            // Создание консьюмеров Kafka
            EventConsumer<MovieEvent> movieConsumer =
                new EventConsumer<>(config.getKafkaBrokers(), "events-service-group", "movie-events");
            EventConsumer<UserEvent> userConsumer =
                new EventConsumer<>(config.getKafkaBrokers(), "events-service-group", "user-events");
            EventConsumer<PaymentEvent> paymentConsumer =
                new EventConsumer<>(config.getKafkaBrokers(), "events-service-group", "payment-events");

            // Запуск консьюмеров
            movieConsumer.start(eventProcessor::processMovieEvent);
            userConsumer.start(eventProcessor::processUserEvent);
            paymentConsumer.start(eventProcessor::processPaymentEvent);

            // Запуск HTTP-сервера
            server.start();

            // Настройка корректного завершения работы
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Завершение работы сервиса событий...");
                movieConsumer.stop();
                userConsumer.stop();
                paymentConsumer.stop();
                eventsHandler.close();
                eventProcessor.close();
                server.stop();
                logger.info("Сервис событий остановлен");
            }));

            logger.info("Сервис событий запущен на порту {}", config.getPort());
        } catch (IOException e) {
            logger.error("Ошибка запуска сервиса событий: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}