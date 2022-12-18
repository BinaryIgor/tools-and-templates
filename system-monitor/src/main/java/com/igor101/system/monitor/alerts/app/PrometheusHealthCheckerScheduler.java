package com.igor101.system.monitor.alerts.app;

import com.igor101.system.monitor.alerts.infra.PrometheusHealthChecker;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PrometheusHealthCheckerScheduler {

    private final PrometheusHealthChecker checker;

    public PrometheusHealthCheckerScheduler(PrometheusHealthChecker checker) {
        this.checker = checker;
    }

    @Scheduled(initialDelayString = "${prometheus-health-check.initial-delay}",
            fixedDelayString = "${prometheus-health-check.fixed-delay}")
    public void schedule() {
        checker.check();
    }
}
