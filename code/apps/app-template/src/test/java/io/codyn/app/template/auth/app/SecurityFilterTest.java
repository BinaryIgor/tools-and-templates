package io.codyn.app.template.auth.app;

import io.codyn.app.template.SpringIntegrationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SecurityFilterTest extends SpringIntegrationTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "99.231.52.22",
            "199.23.52.22",
            "32.0.0.0.1",
            "99.0.0.0.10"
    })
    void shouldReturn401TryingToGetMetricsWithPublicIp(String ip) {
        testHttpClient.test()
                .path("/actuator/health")
                .GET()
                .header(SecurityFilter.REAL_IP_HEADER, ip)
                .execute()
                .expectStatus(401);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "10.131.52.22",
            "10.1.0.0",
            "10.10.10.10",
            "10.129.55.59"
    })
    void shouldReturn200TryingToGetMetricsWithAllowedPrivateIp(String ip) {
        testHttpClient.test()
                .path("/actuator/health")
                .GET()
                .header(SecurityFilter.REAL_IP_HEADER, ip)
                .execute()
                .expectStatusOk();
    }
}
