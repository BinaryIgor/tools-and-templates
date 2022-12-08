package com.igor101.system.monitor.alerts.application;

import com.igor101.system.monitor.alerts.core.PrometheusAlertsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alerts")
public class AlertsController {

    private static final Logger log = LoggerFactory.getLogger(AlertsController.class);

    private final PrometheusAlertsService service;

    public AlertsController(PrometheusAlertsService service) {
        this.service = service;
    }

    @PostMapping
    public void post(@RequestBody PrometheusAlerts alerts) {
        log.info("Receiving prom alerts...{}", alerts.alerts().size());
        service.add(alerts);
    }
}
