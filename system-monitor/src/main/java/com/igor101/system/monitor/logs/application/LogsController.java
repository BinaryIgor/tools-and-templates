package com.igor101.system.monitor.logs.application;

import com.igor101.system.monitor.logs.core.LogsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    void append(@RequestBody List<Map<String, String>> logs) {
        log.info("Receiving some {} logs...", logs.size());
        logsService.handle(ApiLogsMapper.fromApiLogs(logs));
    }
}
