package com.igor101.system.monitor.logs.infra;

import com.igor101.system.monitor.logs.domain.LogsRepository;
import com.igor101.system.monitor.logs.domain.model.LogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileLogsRepository implements LogsRepository {

    static final DateTimeFormatter ROTATED_LOG_FILE_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    static final String LOG_FILE_NAME_PARTS_DELIMITER = "__";
    private static final Pattern LOG_FILE_NAME_PATTERN = Pattern.compile("^(.+?__.+?)([._].*)");
    private static final String LOG_FILE_EXTENSION = ".log";
    private static final Logger log = LoggerFactory.getLogger(FileLogsRepository.class);
    private final Map<LogKey, Object> logsLocks = new ConcurrentHashMap<>();
    private final Clock clock;
    private final String logsRoot;
    private final int maxFileSize;
    private final int maxFiles;

    public FileLogsRepository(Clock clock, String logsRoot, int maxFileSize, int maxFiles) {
        this.clock = clock;
        this.logsRoot = logsRoot;
        this.maxFileSize = maxFileSize;
        this.maxFiles = maxFiles;
    }

    public static String extractedLogFilename(String fullLogFileName) {
        var matcher = LOG_FILE_NAME_PATTERN.matcher(fullLogFileName);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new RuntimeException("%s is not a valid log filename".formatted(fullLogFileName));
    }

    public static Path absoluteLogFilePath(File logsRoot, String source, String application, String instanceId) {
        return Path.of(logsRoot.getAbsolutePath(), application,
                String.join(LOG_FILE_NAME_PARTS_DELIMITER, source, instanceId + LOG_FILE_EXTENSION));
    }

    public static boolean isCurrentFile(String logFile) {
        return logFile.split(LOG_FILE_NAME_PARTS_DELIMITER).length == 2;
    }

    @Override
    public void store(List<LogRecord> logs) {
        for (var e : groupedLogs(logs).entrySet()) {
            try {
                var messages = e.getValue().stream()
                        .map(LogRecord::log)
                        .toList();
                saveLogGroupToFile(e.getKey(), messages);
            } catch (Exception ex) {
                log.error("Problem while saving log({}) to file...", e.getKey(), ex);
                throw new RuntimeException(ex);
            }
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
                var lDir = Path.of(logsRoot, key.application());
                Files.createDirectories(lDir);

                var lFile = new File(lDir.toFile(),
                        String.join(LOG_FILE_NAME_PARTS_DELIMITER, key.source(),
                                key.instanceId()) + LOG_FILE_EXTENSION);

                var lBlock = String.join("\n", logs) + "\n";

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

        var endDateFormatted = ROTATED_LOG_FILE_DATE_TIME_FORMATTER.format(LocalDateTime.now(clock));

        var fileNameWithoutExtension = logFile.getName().replace(LOG_FILE_EXTENSION, "");
        var newName = String.join(LOG_FILE_NAME_PARTS_DELIMITER, fileNameWithoutExtension,
                endDateFormatted) + LOG_FILE_EXTENSION;

        Files.move(logFilePath, logFilePath.resolveSibling(newName));
    }

    @Override
    public void clear() {
        try {
            log.info("About to clean logs in {} dir...", logsRoot);

            var applicationsDirs = Files.list(Path.of(logsRoot)).toList();
            log.info("Have {} applications, cleaning their dirs...", applicationsDirs);

            for (var a : applicationsDirs) clearApplicationLogsDir(a);

            log.info("Logs cleared.");
        } catch (Exception e) {
            throw new RuntimeException("Problem while clearing logs...", e);
        }
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

    private record LogKey(String source, String application, String instanceId) {
    }
}
