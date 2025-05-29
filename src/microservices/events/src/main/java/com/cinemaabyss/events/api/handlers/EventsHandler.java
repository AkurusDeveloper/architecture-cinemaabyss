package com.cinemaabyss.events.api.handlers;

import com.cinemaabyss.events.kafka.EventProducer;
import com.cinemaabyss.events.model.MovieEvent;
import com.cinemaabyss.events.model.PaymentEvent;
import com.cinemaabyss.events.model.UserEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventsHandler {

    private static final Logger logger = LoggerFactory.getLogger(EventsHandler.class);
    private final ObjectMapper objectMapper;
    private final EventProducer<MovieEvent> movieProducer;
    private final EventProducer<UserEvent> userProducer;
    private final EventProducer<PaymentEvent> paymentProducer;

    public EventsHandler(String bootstrapServers) {
        this.objectMapper = new ObjectMapper();
        this.movieProducer = new EventProducer<>(bootstrapServers);
        this.userProducer = new EventProducer<>(bootstrapServers);
        this.paymentProducer = new EventProducer<>(bootstrapServers);
    }

    public void handleMovieEvent(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        String requestBody = readRequestBody(exchange);
        try {
            MovieEvent event = objectMapper.readValue(requestBody, MovieEvent.class);
            movieProducer.send("movie-events", event);

            sendSuccessResponse(exchange);
            logger.info("Обработано событие фильма: {}", event.getId());
        } catch (Exception e) {
            sendErrorResponse(exchange, e);
        }
    }

    public void handleUserEvent(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String requestBody = readRequestBody(exchange);
        try {
            UserEvent event = objectMapper.readValue(requestBody, UserEvent.class);
            userProducer.send("user-events", event);

            sendSuccessResponse(exchange);
            logger.info("Обработано событие пользователя: {}", event.getId());
        } catch (Exception e) {
            sendErrorResponse(exchange, e);
        }
    }

    public void handlePaymentEvent(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String requestBody = readRequestBody(exchange);
        try {
            PaymentEvent event = objectMapper.readValue(requestBody, PaymentEvent.class);
            paymentProducer.send("payment-events", event);

            sendSuccessResponse(exchange);
            logger.info("Обработано событие платежа: {}", event.getId());
        } catch (Exception e) {
            sendErrorResponse(exchange, e);
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                exchange.getRequestBody(),
                StandardCharsets.UTF_8
        ))) {
            return br.lines().collect(Collectors.joining());
        }
    }

    private void sendSuccessResponse(HttpExchange exchange) throws IOException {
        String response = "{\"status\":\"success\"}";
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(201, response.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void sendErrorResponse(HttpExchange exchange, Exception e) throws IOException {
        String response = String.format("{\"status\":\"error\",\"message\":\"%s\"}", e.getMessage());
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(400, response.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        logger.error("Ошибка обработки события: {}", e.getMessage(), e);
    }

    public void close() {
        movieProducer.close();
        userProducer.close();
        paymentProducer.close();
    }
}