package io.codyn.app.template;

import io.codyn.app.template._common.test.TestEmailServer;
import io.codyn.app.template.auth.api.AuthClient;
import io.codyn.app.template.user.common.test.TestUserClient;
import io.codyn.sqldb.core.DSLContextProvider;
import io.codyn.sqldb.test.CustomPostgreSQLContainer;
import io.codyn.test.http.TestHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@Tag("integration")
@ActiveProfiles(value = {"integration"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SpringIntegrationTest.TestConfig.class)
public abstract class SpringIntegrationTest {

    protected static CustomPostgreSQLContainer POSTGRES = CustomPostgreSQLContainer.instance();

    @Autowired
    protected AuthClient authClient;
    @Autowired
    protected TestUserClient userClient;
    @Autowired
    protected TestHttpClient testHttpClient;
    @Autowired
    protected TestEmailServer emailServer;

    protected void setUserAuthToken(UUID id) {
        var token = authClient.ofUser(id).access().value();
        testHttpClient.addBearerAuthorizationHeader(token);
    }

    protected void createUserAndSetAuthToken(UUID id) {
        userClient.createRandomUser(id);
        setUserAuthToken(id);
    }

//    @DynamicPropertySource
//    static void dynamicProperties(DynamicPropertyRegistry registry) {
//        registry.add("logs-storage.file-path", () -> logsRoot.getAbsolutePath());
//    }

    @AfterEach
    protected void tearDown() {
        POSTGRES.clearDb();
        emailServer.clear();
    }


    @TestConfiguration
    static class TestConfig {

        @Bean
        ServerPortListener serverPortListener() {
            return new ServerPortListener();
        }

        @Bean
        TestHttpClient testHttp(ServerPortListener portListener) {
            return new TestHttpClient(portListener::port);
        }

        @Bean
        @Primary
        TestUserClient userClient(DSLContextProvider contextProvider) {
            return new TestUserClient(contextProvider);
        }

        @Bean
        @Primary
        TestEmailServer testEmailServer() {
            return new TestEmailServer();
        }
    }

    static class ServerPortListener {
        private int port;

        public int port() {
            return port;
        }

        @EventListener
        public void onApplicationEvent(ServletWebServerInitializedEvent event) {
            port = event.getWebServer().getPort();
        }
    }
}
