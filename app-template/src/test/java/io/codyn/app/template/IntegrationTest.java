package io.codyn.app.template;

import io.codyn.app.template.test.CustomPostgreSQLContainer;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;

@Tag("integration")
@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = IntegrationTest.TestConfig.class)
public abstract class IntegrationTest {

    protected static CustomPostgreSQLContainer POSTGRES = CustomPostgreSQLContainer.instance();


//    @DynamicPropertySource
//    static void dynamicProperties(DynamicPropertyRegistry registry) {
//        registry.add("logs-storage.file-path", () -> logsRoot.getAbsolutePath());
//    }

    @TestConfiguration
    static class TestConfig {

        //TODO custom config
    }
}
