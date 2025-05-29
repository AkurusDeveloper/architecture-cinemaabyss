package com.cinemaabyss.events.kafka;

import com.cinemaabyss.events.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class EventProcessor {
    private static final Logger logger = LoggerFactory.getLogger(EventProcessor.class);
    private final ObjectMapper objectMapper;
    private final EventProducer<BaseEvent> producer;

    public EventProcessor(String bootstrapServers) {
        this.objectMapper = new ObjectMapper();
        this.producer = new EventProducer<>(bootstrapServers);
    }

    public void processMovieEvent(String json) {
        try {
            MovieEvent event = objectMapper.readValue(json, MovieEvent.class);
            logger.info("Обработка события фильма: id={}, movieId={}, action={}",
                    event.getId(), event.getMovieId(), event.getAction());

            String topic = "movie-events";
            producer.send(topic, event);
        } catch (IOException e) {
            logger.error("Ошибка обработки события фильма: {}", e.getMessage(), e);
        }
    }

    public void processUserEvent(String json) {
        try {
            UserEvent event = objectMapper.readValue(json, UserEvent.class);
            logger.info("Обработка события пользователя: id={}, userId={}, action={}",
                    event.getId(), event.getUserId(), event.getAction());

            String topic = "user-events";
            producer.send(topic, event);
        } catch (IOException e) {
            logger.error("Ошибка обработки события пользователя: {}", e.getMessage(), e);
        }
    }

    public void processPaymentEvent(String json) {
        try {
            PaymentEvent event = objectMapper.readValue(json, PaymentEvent.class);
            logger.info("Обработка события платежа: id={}, paymentId={}, status={}",
                    event.getId(), event.getPaymentId(), event.getStatus());

            String topic = "payment-events";
            producer.send(topic, event);
        } catch (IOException e) {
            logger.error("Ошибка обработки события платежа: {}", e.getMessage(), e);
        }
    }

    public void close() {
        producer.close();
    }
}