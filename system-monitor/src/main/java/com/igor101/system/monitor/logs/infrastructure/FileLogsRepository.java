package com.igor101.system.monitor.logs.infrastructure;

import com.igor101.system.monitor.logs.core.LogsRepository;
import com.igor101.system.monitor.logs.core.model.LogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FileLogsRepository implements LogsRepository {

    static final DateTimeFormatter ROTATED_LOG_FILE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyyMMdd-HHmmss");
    private static final String LOG_FILE_EXTENSION = ".logs";
    private static final Logger log = LoggerFactory.getLogger(FileLogsRepository.class);
    private final Map<LogKey, Object> logsLocks = new ConcurrentHashMap<>();
    private final File logsRoot;
    private final int maxFileSize;

    public FileLogsRepository(File logsRoot, int maxFileSize) {
        this.logsRoot = logsRoot;
        this.maxFileSize = maxFileSize;
    }

    @Override
    public void store(List<LogRecord> logs) {
        var groupedLogs = groupedLogs(logs);

        Exception lastException = null;
        for (var e : groupedLogs.entrySet()) {
            try {
                var messages = e.getValue().stream()
                        .map(LogRecord::log)
                        .toList();
                saveLogGroupToFile(e.getKey(), messages);
            } catch (Exception ex) {
                log.error("Problem while saving log({}) to file...", e.getKey(), ex);
                lastException = ex;
            }
        }

        if (lastException != null) {
            throw new RuntimeException(lastException);
        }
    }

    private Map<LogKey, List<LogRecord>> groupedLogs(List<LogRecord> logs) {
        return logs.stream()
                .collect(Collectors.groupingBy(e ->
                        new LogKey(e.source(), e.application(), e.instanceId())));
    }

    private void saveLogGroupToFile(LogKey key, List<String> logs) {
        synchronized (lockForLogsGroup(key)) {
            try {
                var lDir = Path.of(logsRoot.getAbsolutePath(), key.application());
                Files.createDirectories(lDir);

                var lFile = new File(lDir.toFile(),
                        "%s_%s_%s".formatted(key.application(), key.source(),
                                key.instanceId())
                                + LOG_FILE_EXTENSION);

                var lBlock = String.join("\n", logs);

                if (lFile.exists() && shouldRotateLogFile(lFile, lBlock)) {
                    rotateLogFile(lFile);
                }

                Files.writeString(lFile.toPath(), lBlock, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (Exception e) {
                log.error("Failed to save logs group for {} key..", key, e);
            }
        }
    }

    private Object lockForLogsGroup(LogKey logKey) {
        return logsLocks.computeIfAbsent(logKey, k -> new Object());
    }

    private boolean shouldRotateLogFile(File logFile, String newContent) {
        return (logFile.length() + newContent.getBytes().length) >= maxFileSize;
    }

    private void rotateLogFile(File logFile) throws Exception {
        var logFilePath = logFile.toPath();

        var endDateFormatted = ROTATED_LOG_FILE_DATE_TIME_FORMATTER.format(LocalDateTime.now(Clock.systemUTC()));

        var fileNameWithoutExtension = logFile.getName().replace(LOG_FILE_EXTENSION, "");
        var newName = "%s_%s%s".formatted(fileNameWithoutExtension, endDateFormatted, LOG_FILE_EXTENSION);

        Files.move(logFilePath, logFilePath.resolveSibling(newName));
    }

    private record LogKey(String source, String application, String instanceId) {
    }
}
