package com.igor101.system.monitor.logs.application;

import com.igor101.system.monitor.logs.core.LogsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/logs")
public class LogsController {

    private static final Logger log = LoggerFactory.getLogger(LogsController.class);

    private final LogsService logsService;

    public LogsController(LogsService logsService) {
        this.logsService = logsService;
    }

    @PostMapping
    void append(@RequestBody LogsApi logsApi) {
        log.info("Receiving some {} logs from {} source...", logsApi.logs().size(), logsApi.source());
        var logs = logsApi.logs().stream()
                .map(l -> l.toLogData(logsApi.source()))
                .toList();

        logsService.handle(logs, Instant.now());
    }

    @PostMapping("/fluentd")
    void appendFluentd(@RequestBody List<Map<String, Object>> logs) {
        log.info("Receiving some logs...{}", logs.size());
    }
}
