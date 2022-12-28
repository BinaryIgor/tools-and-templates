package io.codyn.commons.sqldb.test;

import org.testcontainers.containers.PostgreSQLContainer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;

public class CustomPostgreSQLContainer extends PostgreSQLContainer<CustomPostgreSQLContainer> {

    private static final String SCHEMAS_ROOT_DIR = "db";
    private static final String SCHEMAS_DIR = "schemas";
    private static final String SCHEMAS_ORDER_FILE = "schemas_order.txt";
    private static final String CLEAR_DB_FILE = "clear_db.sql";
    private static final String IMAGE_VERSION = "postgres:14";
    private static CustomPostgreSQLContainer instance;
    private String schemasPath;
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

    //TODO: refactor
    private void initSchema() {
        findSchemasPath();

        try {
            var schemas = Files.readString(Path.of(schemasPath, SCHEMAS_ORDER_FILE)).split(" ");
            for (var s : schemas) {
                migrateSchema(s);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void findSchemasPath() {
        var path = Paths.get("").toAbsolutePath();

        for (int i = 0; i < 10; ++i) {
            var candidatePath = Paths.get(path.toString(), SCHEMAS_ROOT_DIR, SCHEMAS_DIR);
            if (Files.exists(candidatePath)) {
                schemasPath = candidatePath.toAbsolutePath().toString();
                break;
            }

            path = path.getParent();
            if (path == null) {
                break;
            }
        }

        if (schemasPath == null) {
            throw new RuntimeException("Can't find schemas path");
        }
    }

    private void migrateSchema(String schema) throws Exception {
        Files.list(Path.of(schemasPath, schema))
                .forEach(m -> {
                    try {
                        System.out.println("Applying migration..." + m);
                        var migration = Files.readString(m);

                        var toExecuteMigration = """
                                CREATE SCHEMA IF NOT EXISTS "%s";
                                SET SEARCH_PATH="%s",public;
                                %s
                                SET SEARCH_PATH=public;
                                """.formatted(schema, schema, migration);

                        System.out.println(toExecuteMigration);

                        connection.prepareStatement(toExecuteMigration).execute();
                    } catch (Exception e) {
                        throw new RuntimeException("Problem while initializing schema: %s".formatted(schema), e);
                    }
                });
    }

    public void clearDb() {
        try {
            var clearScript = Files.readString(Path.of(schemasPath, CLEAR_DB_FILE));
            connection.prepareStatement(clearScript)
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
