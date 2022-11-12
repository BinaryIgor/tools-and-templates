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

public class FileLogsRepository implements LogsRepository {

    static final DateTimeFormatter ROTATED_LOG_FILE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyyMMdd-HHmmss");
    private static final String LOG_FILE_EXTENSION = ".logs";
    private static final Logger log = LoggerFactory.getLogger(FileLogsRepository.class);
    private final File logsRoot;
    private final int maxFileSize;

    public FileLogsRepository(File logsRoot, int maxFileSize) {
        this.logsRoot = logsRoot;
        this.maxFileSize = maxFileSize;
    }

    @Override
    public void store(List<LogRecord> logs) {
        Exception lastException = null;
        for (var l : logs) {
            try {
                saveLogToFile(l);
            } catch (Exception e) {
                log.error("Problem while saving log {}:{}:{} to file...",
                        l.application(), l.source(), l.instanceId(), e);
                lastException = e;
            }
        }

        if (lastException != null) {
            throw new RuntimeException(lastException);
        }
    }

    private void saveLogToFile(LogRecord logRecord) {
        try {
            var lDir = Path.of(logsRoot.getAbsolutePath(), logRecord.application());
            Files.createDirectories(lDir);

            var lFile = new File(lDir.toFile(),
                    "%s_%s_%s".formatted(logRecord.application(), logRecord.source(),
                            logRecord.instanceId())
                            + LOG_FILE_EXTENSION);

            var lBlock = """
                    receivedTimestamp: %s
                    fromTimestamp: %s
                    toTimestamp: %s
                    level: %s
                                            
                    %s
                    """.formatted(logRecord.receivedTimestamp(), logRecord.fromTimestamp(), logRecord.toTimestamp(),
                    logRecord.level(), logRecord.log());

            if (lFile.exists() && shouldRotateLogFile(lFile, lBlock)) {
                rotateLogFile(lFile);
            }

            Files.writeString(lFile.toPath(), lBlock, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {
            log.error("Failed to save logs for {} application...", logRecord.application(), e);
        }
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
}
