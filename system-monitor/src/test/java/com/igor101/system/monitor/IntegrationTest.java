package com.igor101.system.monitor;

import com.igor101.system.monitor.test.TestHttp;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;

@Tag("integration")
@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = IntegrationTest.TestConfig.class)
public abstract class IntegrationTest {

    @TempDir
    protected static File logsRoot;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("logs-storage.file-path", () -> logsRoot.getAbsolutePath());
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        TestHttp testHttp(TestRestTemplate template) {
            return new TestHttp(template);
        }
    }
}
