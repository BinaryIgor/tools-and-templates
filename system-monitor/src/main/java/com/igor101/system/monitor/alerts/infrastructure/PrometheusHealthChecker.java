package com.igor101.system.monitor.alerts.infrastructure;

import com.igor101.system.monitor.alerts.application.AlertsController;
import com.igor101.system.monitor.common.Metrics;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

//Additional, optional prometheus check
@Component
public class PrometheusHealthChecker {

    static final String PROMETHEUS_DOWN_TIMESTAMP = Metrics.fullName("prometheus_down_timestamp_seconds");
    private static final Logger log = LoggerFactory.getLogger(AlertsController.class);
    private final AtomicReference<Double> prometheusDownTimestampGauge = new AtomicReference<>();
    private final HttpClient httpClient;
    private final MeterRegistry meterRegistry;
    private final String prometheusHealthCheckUrl;
    private final int retryTimes;
    private final int retryInterval;

    public PrometheusHealthChecker(HttpClient httpClient,
                                   MeterRegistry meterRegistry,
                                   String prometheusHealthCheckUrl,
                                   int retryTimes,
                                   int retryInterval) {
        this.httpClient = httpClient;
        this.meterRegistry = meterRegistry;
        this.prometheusHealthCheckUrl = prometheusHealthCheckUrl;
        this.retryTimes = retryTimes;
        this.retryInterval = retryInterval;
    }

    @Autowired
    public PrometheusHealthChecker(MeterRegistry meterRegistry,
                                   @Value("${prometheus-health-check.url}") String prometheusHealthCheckUrl,
                                   @Value("${prometheus-health-check.retry-times:5}") int retryTimes,
                                   @Value("${prometheus-health-check.retry-interval:3000}") int retryInterval) {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build(),
                meterRegistry,
                prometheusHealthCheckUrl,
                retryInterval,
                retryTimes);
    }

    public void check() {
        try {
            var request = prometheusHealthCheckRequest();

            if (checkPrometheusHealth(request)) {
                log.info("Prometheus is UP!");
            } else {
                log.error("Prometheus is DOWN!");
                prometheusDownTimestampGauge.set(System.currentTimeMillis() / 1000.0);
                meterRegistry.gauge(PROMETHEUS_DOWN_TIMESTAMP, prometheusDownTimestampGauge, AtomicReference::get);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest prometheusHealthCheckRequest() throws Exception {
        return HttpRequest.newBuilder()
                .uri(new URI(prometheusHealthCheckUrl))
                .GET()
                .build();
    }

    private boolean checkPrometheusHealth(HttpRequest request) {
        for (int i = 0; i < retryTimes; i++) {
            try {
                var response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    return true;
                }
                Thread.sleep(retryInterval);
            } catch (Exception ignored) {

            }
        }

        return false;
    }
}
