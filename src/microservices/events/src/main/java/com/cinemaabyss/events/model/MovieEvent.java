package com.cinemaabyss.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MovieEvent extends BaseEvent {
    @JsonProperty("movie_id")
    private Integer movieId;
    @JsonProperty("user_id")
    private Integer userId;
    private String title;
    private String action;
    private Double rating;

    public MovieEvent() {
        super();
    }

    public MovieEvent(Integer movieId, Integer userId, String title, String action) {
        super(determineEventType(action));
        this.movieId = movieId;
        this.userId = userId;
        this.title = title;
        this.action = action;
    }

    private static EventType determineEventType(String action) {
        switch (action) {
            case "viewed": return EventType.MOVIE_VIEW;
            case "rated": return EventType.MOVIE_RATE;
            case "added": return EventType.MOVIE_ADD;
            default: throw new IllegalArgumentException("Unknown movie action: " + action);
        }
    }

    // Геттеры и сеттеры
    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
        setType(determineEventType(action));
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}