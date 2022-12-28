package io.codyn.app.template;

import io.codyn.app.template.test.TestHttp;
import io.codyn.app.template.user.TestUserClient;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import io.codyn.commons.sqldb.test.CustomPostgreSQLContainer;

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

        @Bean
        @Primary
        TestUserClient userClient(DSLContext context) {
            return new TestUserClient(context);
        }
    }
}
