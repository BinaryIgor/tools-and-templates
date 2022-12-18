package io.codyn.app.template.test;

import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.DriverManager;

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
            instance.initSchema();
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

    //Probably tmp solution, figure out proper migration approach
    private void initSchema() {
        try (var connection = DriverManager.getConnection(getJdbcUrl(), getUsername(), getPassword())) {
            var schema = readSchema();
            connection.prepareStatement(schema)
                    .execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String readSchema() throws Exception {
        try (var is = getClass().getResourceAsStream("/schema.sql")) {
            return new String(is.readAllBytes());
        }
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
