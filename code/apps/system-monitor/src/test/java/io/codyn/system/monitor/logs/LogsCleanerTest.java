package io.codyn.system.monitor.logs;

import io.codyn.system.monitor.IntegrationTest;
import io.codyn.test.TestRandom;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LogsCleanerTest extends IntegrationTest {

    @Autowired
    private LogsCleaner logsCleaner;

    @Test
    void shouldClearOldestLogFiles() throws Exception {
        createLogFile("first-app", "first-machine.log");
        createLogFile("first-app", "first-machine__20221212-140000.log");
        createLogFile("first-app", "first-machine__20221212-120000.log");
        createLogFile("first-app", "first-machine__20221212-100000.log");
        createLogFile("first-app", "another-machine.log");

        createLogFile("second-app", "machine.log");
        createLogFile("second-app", "machine__2022100101-101010.log");
        createLogFile("second-app", "machine__2021100101-101010.log");
        createLogFile("second-app", "machine__2020100101-101010.log");

        logsCleaner.clear();

        assertDirHasOnlyFiles("first-app",
                List.of("first-machine.log",
                        "first-machine__20221212-140000.log",
                        "first-machine__20221212-120000.log",
                        "another-machine.log"));

        assertDirHasOnlyFiles("second-app",
                List.of("machine.log",
                        "machine__2022100101-101010.log",
                        "machine__2021100101-101010.log"));
    }

    private void createLogFile(String dir, String filename) throws Exception {
        var fileDir = Path.of(logsRoot.getAbsolutePath(), dir);
        Files.createDirectories(fileDir);
        Files.writeString(Path.of(fileDir.toString(), filename), TestRandom.string());
    }

    private void assertDirHasOnlyFiles(String dir, List<String> expectedFiles) throws Exception {
        var actualFiles = Files.list(Path.of(logsRoot.getAbsolutePath(), dir))
                .map(p -> p.getFileName().toString())
                .toList();

        Assertions.assertThat(actualFiles)
                .containsExactlyInAnyOrderElementsOf(expectedFiles);
    }
}
