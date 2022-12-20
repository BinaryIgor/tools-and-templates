package io.codyn.app.template;

import io.codyn.app.template.test.CustomPostgreSQLContainer;
import io.codyn.app.template.test.TestHttp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@Tag("integration")
@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringIntegrationTest.TestConfig.class)
public abstract class SpringIntegrationTest {

    protected static CustomPostgreSQLContainer POSTGRES = CustomPostgreSQLContainer.instance();


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
        public TestHttp testHttp(TestRestTemplate restTemplate) {
            return new TestHttp(restTemplate);
        }
    }
}
