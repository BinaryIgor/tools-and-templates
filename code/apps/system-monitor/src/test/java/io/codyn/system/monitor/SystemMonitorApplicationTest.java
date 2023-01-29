package io.codyn.system.monitor;

import io.codyn.system.monitor._shared.app.ExceptionResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;

public class SystemMonitorApplicationTest extends IntegrationTest {

    @Test
    void shouldLoadContext() {
        System.out.println("Testing context load...");
    }

    @Test
    void shouldReturnExceptionResponseWithInvalidRequest() {
        var response = testHttpClient.test()
                .path("/logs")
                .POST()
                .expectStatus(400)
                .executeReturningObject(ExceptionResponse.class);

        Assertions.assertThat(response.error())
                .isEqualTo(HttpMessageNotReadableException.class.getSimpleName());
        Assertions.assertThat(response.message())
                .contains("Required request body is missing");
    }
}
