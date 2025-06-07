package com.cinemaabyss.events.api;

import com.cinemaabyss.events.api.handlers.EventsHandler;
import com.cinemaabyss.events.api.handlers.HealthHandler;
import com.cinemaabyss.events.config.Config;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final HttpServer server;

    public Server(Config config, EventsHandler eventsHandler, HealthHandler healthHandler) throws IOException {
        int port = config.getPort();
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(10));

        // Регистрация обработчиков
        server.createContext("/api/events/health", healthHandler);
        server.createContext("/api/events/movie", eventsHandler::handleMovieEvent);
        server.createContext("/api/events/user", eventsHandler::handleUserEvent);
        server.createContext("/api/events/payment", eventsHandler::handlePaymentEvent);

        logger.info("HTTP-сервер инициализирован на порту {}", port);
    }

    public void start() {
        server.start();
        logger.info("HTTP-сервер запущен");
    }

    public void stop() {
        server.stop(0);
        logger.info("HTTP-сервер остановлен");
    }
}