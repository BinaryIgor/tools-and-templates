package io.codyn.app.template;

import io.codyn.app.template.test.CustomPostgreSQLContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;

@Tag("integration")
public abstract class DbTest {

    protected static CustomPostgreSQLContainer POSTGRES = CustomPostgreSQLContainer.instance();

    @AfterEach
    protected void tearDown() {
        POSTGRES.clearDb();
    }
}
