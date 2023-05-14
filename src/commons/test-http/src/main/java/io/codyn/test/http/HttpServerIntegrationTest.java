package io.codyn.test.http;

import org.junit.jupiter.api.*;

@Tag("integration")
public abstract class HttpServerIntegrationTest {

    protected final static TestHttpServer HTTP_SERVER = new TestHttpServer();

    @BeforeAll
    static void startHttpServer() {
        HTTP_SERVER.start();
    }

    @BeforeEach
    void beforeEach() {
        setup();
    }

    protected void setup() {

    }

    @AfterEach
    void afterEach() {
        HTTP_SERVER.reset();
    }

    @AfterAll
    static void stopHttpServer() {
        HTTP_SERVER.stop();
    }
}
