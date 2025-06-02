package com.cinemaabyss.proxy;

public class Config {
    private final String port;
    private final String monolithUrl;
    private final String moviesServiceUrl;
    private final String eventsServiceUrl;
    private final boolean gradualMigration;
    private final int moviesMigrationPercent;

    public Config() {
        this.port = getEnv("PORT", "8000");
        this.monolithUrl = getEnv("MONOLITH_URL", "http://monolith:8080");
        this.moviesServiceUrl = getEnv("MOVIES_SERVICE_URL", "http://movies-service:8081");
        this.eventsServiceUrl = getEnv("EVENTS_SERVICE_URL", "http://events-service:8082");
        this.gradualMigration = Boolean.parseBoolean(getEnv("GRADUAL_MIGRATION", "false"));
        this.moviesMigrationPercent = Integer.parseInt(getEnv("MOVIES_MIGRATION_PERCENT", "0"));
    }

    private String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return value != null ? value : defaultValue;
    }

    public String getPort() { return port; }
    public String getMonolithUrl() { return monolithUrl; }
    public String getMoviesServiceUrl() { return moviesServiceUrl; }
    public String getEventsServiceUrl() { return eventsServiceUrl; }
    public boolean isGradualMigration() { return gradualMigration; }
    public int getMoviesMigrationPercent() { return moviesMigrationPercent; }
}