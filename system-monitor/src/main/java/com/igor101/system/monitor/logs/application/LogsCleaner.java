package com.igor101.system.monitor.logs.application;

import com.igor101.system.monitor.logs.infrastructure.FileLogsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LogsCleaner {

    private static final Logger log = LoggerFactory.getLogger(LogsCleaner.class);
    private final String logsRoot;
    private final int maxFiles;

    public LogsCleaner(String logsRoot, int maxFiles) {
        this.logsRoot = logsRoot;
        this.maxFiles = maxFiles;
    }

    @Scheduled(initialDelayString = "${logs-cleaner.initial-delay}",
            fixedDelayString = "${logs-cleaner.fixed-delay}")
    public void clear() throws Exception {
        log.info("About to clean logs in {} dir...", logsRoot);

        var applicationsDirs = Files.list(Path.of(logsRoot)).toList();
        log.info("Have {} applications, cleaning their dirs...", applicationsDirs);

        for (var a : applicationsDirs) {
            try {
                clearApplicationLogsDir(a);
            } catch (Exception e) {
                log.error("Problem while cleaning logs of {} application...", a, e);
            }
        }

        log.info("Logs cleared.");
    }

    private void clearApplicationLogsDir(Path dir) throws Exception {
        var groupedFiles = Files.list(dir)
                .collect(Collectors.groupingBy(p -> FileLogsRepository.extractedLogFilename(p.toString())));

        for (var e : groupedFiles.entrySet()) {
            var toDeleteFiles = e.getValue().stream()
                    .map(p -> p.toAbsolutePath().toString())
                    .filter(n -> !FileLogsRepository.isCurrentFile(n))
                    .sorted(Collections.reverseOrder())
                    .skip(maxFiles)
                    .toList();

            if (!toDeleteFiles.isEmpty()) {
                deleteFiles(toDeleteFiles);
                log.info("{} log files deleted", toDeleteFiles);
            }
        }
    }

    private void deleteFiles(List<String> files) throws Exception {
        for (var f : files) Files.delete(Path.of(f));
    }
}
