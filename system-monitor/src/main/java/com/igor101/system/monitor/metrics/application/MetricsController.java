package com.igor101.system.monitor.metrics.application;

import com.igor101.system.monitor.metrics.core.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/metrics")
public class MetricsController {

    private static final Logger log = LoggerFactory.getLogger(MetricsController.class);
    private final MetricsService service;

    public MetricsController(MetricsService service) {
        this.service = service;
    }

    @PostMapping
    public void add(@RequestBody ContainersMetrics metrics) {
        log.info("Received {} metrics from {}", metrics.metrics().size(), metrics.source());
        service.add(metrics);
    }
}
