package com.cinemaabyss.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserEvent extends BaseEvent {
    @JsonProperty("user_id")
    private Integer userId;
    private String username;
    private String action;


    public UserEvent() {
        super();
    }

    public UserEvent(Integer userId, String username, String action) {
        super(determineEventType(action));
        this.userId = userId;
        this.username = username;
        this.action = action;
    }

    private static EventType determineEventType(String action) {
        switch (action) {
            case "registered": return EventType.USER_REGISTER;
            case "logged_in": return EventType.USER_LOGIN;
            default: throw new IllegalArgumentException("Unknown user action: " + action);
        }
    }

    // Геттеры и сеттеры
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
        setType(determineEventType(action));
    }
}