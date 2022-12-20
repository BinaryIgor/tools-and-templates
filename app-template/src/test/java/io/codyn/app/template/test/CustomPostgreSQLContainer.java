package io.codyn.app.template.test;

import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;

public class CustomPostgreSQLContainer extends PostgreSQLContainer<CustomPostgreSQLContainer> {

    private static final String IMAGE_VERSION = "postgres:14";
    private static CustomPostgreSQLContainer instance;
    private Connection connection;

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

        try {
            connection = DriverManager.getConnection(getJdbcUrl(), getUsername(), getPassword());
        } catch (Exception e) {
            throw new RuntimeException("Can't connect!", e);
        }
    }

    //Probably tmp solution, figure out proper migration approach
    private void initSchema() {
        try {
            var schema = classPathResource("schema.sql");
            connection.prepareStatement(schema)
                    .execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String classPathResource(String resource) throws Exception {
        try (var is = getClass().getResourceAsStream("/" + resource)) {
            return new String(is.readAllBytes());
        }
    }

    public void clearDb() {
        try {
            var schema = classPathResource("clear-db.sql");
            connection.prepareStatement(schema)
                    .execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
