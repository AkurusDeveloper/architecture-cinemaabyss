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

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestampFromString(String timestamp) {
        if (timestamp != null && !timestamp.isEmpty()) {
            setTimestamp(Instant.parse(timestamp));
        }
    }
}