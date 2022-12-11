package com.igor101.system.monitor.logs.infrastructure;

import com.igor101.system.monitor.test.Tests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

public class FileLogsRepositoryTest {

    @TempDir
    File root;

    private FileLogsRepository repository;
    private File logsRoot;
    private int maxFileSize;

    @BeforeEach
    void setup() {
        maxFileSize = Tests.randomInt(100, 1000);
        repository = new FileLogsRepository(logsRoot, maxFileSize);
    }

    @Test
    void shouldStoreNewLogs() {

    }
}
