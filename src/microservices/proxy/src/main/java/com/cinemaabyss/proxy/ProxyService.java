package com.cinemaabyss.proxy;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProxyService {
    private static final Logger logger = Logger.getLogger(ProxyService.class.getName());
    private static final Random random = new Random();
    private final Config config;
    private final CloseableHttpClient httpClient;

    public ProxyService() {
        this.config = new Config();
        this.httpClient = HttpClients.createDefault();
    }

    public void start() throws IOException {
        int port = Integer.parseInt(config.getPort());
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this::handleRequest);
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();

        logger.info("Прокси-сервер запущен на порту " + port);
        logger.info("Градуальная миграция: " + (config.isGradualMigration() ? "включена" : "выключена"));
        if (config.isGradualMigration()) {
            logger.info("Процент запросов к микросервису фильмов: " + config.getMoviesMigrationPercent() + "%");
        }
    }

    private void handleRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String targetUrl;

        // Определяем URL назначения
        if (path.startsWith("/api/events/")) {
            // Запросы к событиям всегда идут в микросервис событий
            targetUrl = config.getEventsServiceUrl() + path;
            logger.info("Маршрутизация к микросервису событий: " + path);
        } else if (path.startsWith("/api/movies/")) {
            // Для фильмов применяем постепенную миграцию
            if (config.isGradualMigration()) {
                int randomValue = random.nextInt(100);
                if (randomValue < config.getMoviesMigrationPercent()) {
                    targetUrl = config.getMoviesServiceUrl() + path;
                    logger.info("Маршрутизация к микросервису фильмов: " + path);
                } else {
                    targetUrl = config.getMonolithUrl() + path;
                    logger.info("Маршрутизация к монолиту (фильмы): " + path);
                }
            } else {
                // Если постепенная миграция отключена, всегда идём в микросервис
                targetUrl = config.getMoviesServiceUrl() + path;
                logger.info("Маршрутизация к микросервису фильмов: " + path);
            }
        } else {
            // Все остальные запросы идут в монолит
            targetUrl = config.getMonolithUrl() + path;
            logger.info("Маршрутизация к монолиту: " + path);
        }

        // Проксирование запроса
        proxyRequest(exchange, targetUrl);
    }

    private void proxyRequest(HttpExchange exchange, String targetUrl) throws IOException {
        String method = exchange.getRequestMethod();
        URI uri = URI.create(targetUrl);

        // Создаем соответствующий HTTP-запрос
        HttpRequestBase request;
        switch (method) {
            case "GET":
                request = new HttpGet(uri);
                break;
            case "POST":
                HttpPost postRequest = new HttpPost(uri);
                postRequest.setEntity(new InputStreamEntity(exchange.getRequestBody()));
                request = postRequest;
                break;
            case "PUT":
                HttpPut putRequest = new HttpPut(uri);
                putRequest.setEntity(new InputStreamEntity(exchange.getRequestBody()));
                request = putRequest;
                break;
            case "DELETE":
                request = new HttpDelete(uri);
                break;
            default:
                exchange.sendResponseHeaders(405, -1);
                return;
        }

        // Копируем заголовки
        exchange.getRequestHeaders().forEach((key, values) -> {
            if (!key.equalsIgnoreCase("Host")) { // Не копируем заголовок Host
                for (String value : values) {
                    request.addHeader(key, value);
                }
            }
        });

        // Выполняем запрос
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            // Копируем статус ответа
            int statusCode = response.getStatusLine().getStatusCode();
            exchange.sendResponseHeaders(statusCode, 0);

            // Копируем заголовки ответа
            for (org.apache.http.Header header : response.getAllHeaders()) {
                exchange.getResponseHeaders().add(header.getName(), header.getValue());
            }

            // Копируем тело ответа
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream is = entity.getContent();
                     OutputStream os = exchange.getResponseBody()) {
                    IOUtils.copy(is, os);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при проксировании запроса", e);
            exchange.sendResponseHeaders(500, -1);
        } finally {
            exchange.close();
        }
    }

    public static void main(String[] args) {
        try {
            new ProxyService().start();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Не удалось запустить прокси-сервер", e);
        }
    }
}