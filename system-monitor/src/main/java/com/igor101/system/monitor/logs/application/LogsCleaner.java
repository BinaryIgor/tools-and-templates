package com.igor101.system.monitor.logs.application;

import com.igor101.system.monitor.logs.core.LogsRepository;
import org.springframework.scheduling.annotation.Scheduled;

public class LogsCleaner {

    private final LogsRepository logsRepository;

    public LogsCleaner(LogsRepository logsRepository) {
        this.logsRepository = logsRepository;
    }

    @Scheduled(initialDelayString = "${logs-cleaner.initial-delay}",
            fixedDelayString = "${logs-cleaner.fixed-delay}")
    public void clear() {
        logsRepository.clear();
    }
}
