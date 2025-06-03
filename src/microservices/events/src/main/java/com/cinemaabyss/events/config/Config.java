package com.cinemaabyss.events.config;

public class Config {
    private final int port;
    private final String kafkaBrokers;

    public Config() {
        this.port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8082"));
        this.kafkaBrokers = System.getenv().getOrDefault("EVENTS_SERVICE__KAFKA_BROKERS", "localhost:9092");
    }

    public int getPort() {
        return port;
    }

    public String getKafkaBrokers() {
        return kafkaBrokers;
    }
}