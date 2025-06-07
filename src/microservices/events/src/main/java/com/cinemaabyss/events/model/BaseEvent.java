package com.cinemaabyss.events.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseEvent {
    private String id;
    private EventType type;
    private Instant timestamp;

    public BaseEvent() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
    }

    public BaseEvent(EventType type) {
        this();
        this.type = type;
    }

    // Геттеры и сеттеры
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp.toEpochMilli();
    }

    public void setTimestamp(Long timestampMillis) {
        if (timestampMillis != null) {
            this.timestamp = Instant.ofEpochMilli(timestampMillis);
        }
    }
    @JsonProperty("timestamp")
    public void setTimestampFromString(String timestamp) {
        if (timestamp != null && !timestamp.isEmpty()) {
            try {
                // Пробуем интерпретировать как число миллисекунд
                this.timestamp = Instant.ofEpochMilli(Long.parseLong(timestamp));
            } catch (NumberFormatException e) {
                // Если не получилось, пробуем как ISO-8601
                this.timestamp = Instant.parse(timestamp);
            }
        }
    }
}