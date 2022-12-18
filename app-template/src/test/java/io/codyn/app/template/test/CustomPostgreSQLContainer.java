package io.codyn.app.template.test;

import org.testcontainers.containers.PostgreSQLContainer;

public class CustomPostgreSQLContainer extends PostgreSQLContainer<CustomPostgreSQLContainer> {

    private static final String IMAGE_VERSION = "postgres:14";
    private static CustomPostgreSQLContainer instance;

    private CustomPostgreSQLContainer() {
        super(IMAGE_VERSION);
    }

    public static CustomPostgreSQLContainer instance() {
        if (instance == null) {
            instance = new CustomPostgreSQLContainer();
            instance.start();
        }
        return instance;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", instance.getJdbcUrl());
        System.setProperty("DB_USERNAME", instance.getUsername());
        System.setProperty("DB_PASSWORD", instance.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
