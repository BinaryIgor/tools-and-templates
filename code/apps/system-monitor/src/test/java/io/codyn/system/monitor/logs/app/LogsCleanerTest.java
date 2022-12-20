package io.codyn.system.monitor.logs.app;

import io.codyn.system.monitor.IntegrationTest;
import io.codyn.system.monitor.test.Tests;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LogsCleanerTest extends IntegrationTest {

    @Test
    void shouldClearOldestLogFiles() throws Exception {
        createLogFile("first-app", "first-machine__instance1.log");
        createLogFile("first-app", "first-machine__instance1__20221212-140000.log");
        createLogFile("first-app", "first-machine__instance1__20221212-120000.log");
        createLogFile("first-app", "first-machine__instance1__20221212-100000.log");
        createLogFile("first-app", "first-machine__instance3.log");
        createLogFile("first-app", "another-machine__instance1.log");

        createLogFile("second-app", "machine__instance.log");
        createLogFile("second-app", "machine__instance__2022100101-101010.log");
        createLogFile("second-app", "machine__instance__2021100101-101010.log");
        createLogFile("second-app", "machine__instance__2020100101-101010.log");

        //wait for scheduled clear
        Thread.sleep(1000);

        assertDirHasOnlyFiles("first-app",
                List.of("first-machine__instance1.log",
                        "first-machine__instance1__20221212-140000.log",
                        "first-machine__instance1__20221212-120000.log",
                        "first-machine__instance3.log",
                        "another-machine__instance1.log"));

        assertDirHasOnlyFiles("second-app",
                List.of("machine__instance.log",
                        "machine__instance__2022100101-101010.log",
                        "machine__instance__2021100101-101010.log"));
    }

    private void createLogFile(String dir, String filename) throws Exception {
        var fileDir = Path.of(logsRoot.getAbsolutePath(), dir);
        Files.createDirectories(fileDir);
        Files.writeString(Path.of(fileDir.toString(), filename), Tests.randomString());
    }

    private void assertDirHasOnlyFiles(String dir, List<String> expectedFiles) throws Exception {
        var actualFiles = Files.list(Path.of(logsRoot.getAbsolutePath(), dir))
                .map(p -> p.getFileName().toString())
                .toList();

        Assertions.assertThat(actualFiles)
                .containsExactlyInAnyOrderElementsOf(expectedFiles);
    }
}
