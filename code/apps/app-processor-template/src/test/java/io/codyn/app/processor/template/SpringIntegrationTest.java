package io.codyn.app.processor.template;

import io.codyn.app.processor.template.user.test.TestSqlUserClient;
import io.codyn.sqldb.core.DSLContextProvider;
import io.codyn.sqldb.test.CustomPostgreSQLContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@Tag("integration")
@ActiveProfiles(value = {"integration"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = SpringIntegrationTest.TestConfig.class)
public abstract class SpringIntegrationTest {

    protected static CustomPostgreSQLContainer POSTGRES = CustomPostgreSQLContainer.instance();

    @Autowired
    protected TestSqlUserClient userClient;

//    @DynamicPropertySource
//    static void dynamicProperties(DynamicPropertyRegistry registry) {
//        registry.add("logs-storage.file-path", () -> logsRoot.getAbsolutePath());
//    }

    @AfterEach
    protected void tearDown() {
        POSTGRES.clearDb();
    }


    @TestConfiguration
    static class TestConfig {

        @Bean
        TestSqlUserClient userClient(DSLContextProvider contextProvider) {
            return new TestSqlUserClient(contextProvider);
        }

    }
}
