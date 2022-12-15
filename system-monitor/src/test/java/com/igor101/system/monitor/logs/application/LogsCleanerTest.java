package com.igor101.system.monitor.logs.application;

import com.igor101.system.monitor.test.Tests;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogsCleanerTest {

    @TempDir
    static File root;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("logs-storage.file-path", () -> root.getAbsolutePath());
    }

    @Test
    void shouldClearOldestLogFiles() throws Exception {
        createLogFile("first-application", "first-machine__instance1.log");
        createLogFile("first-application", "first-machine__instance1__20221212-140000.log");
        createLogFile("first-application", "first-machine__instance1__20221212-120000.log");
        createLogFile("first-application", "first-machine__instance1__20221212-100000.log");
        createLogFile("first-application", "first-machine__instance3.log");
        createLogFile("first-application", "another-machine__instance1.log");

        createLogFile("second-application", "machine__instance.log");
        createLogFile("second-application", "machine__instance__2022100101-101010.log");
        createLogFile("second-application", "machine__instance__2021100101-101010.log");
        createLogFile("second-application", "machine__instance__2020100101-101010.log");

        //wait for scheduled clear
        Thread.sleep(1000);

        assertDirHasOnlyFiles("first-application",
                List.of("first-machine__instance1.log",
                        "first-machine__instance1__20221212-140000.log",
                        "first-machine__instance1__20221212-120000.log",
                        "first-machine__instance3.log",
                        "another-machine__instance1.log"));

        assertDirHasOnlyFiles("second-application",
                List.of("machine__instance.log",
                        "machine__instance__2022100101-101010.log",
                        "machine__instance__2021100101-101010.log"));
    }

    private void createLogFile(String dir, String filename) throws Exception {
        var fileDir = Path.of(root.getAbsolutePath(), dir);
        Files.createDirectories(fileDir);
        Files.writeString(Path.of(fileDir.toString(), filename), Tests.randomString());
    }

    private void assertDirHasOnlyFiles(String dir, List<String> expectedFiles) throws Exception {
        var actualFiles = Files.list(Path.of(root.getAbsolutePath(), dir))
                .map(p -> p.getFileName().toString())
                .toList();

        Assertions.assertThat(actualFiles)
                .containsExactlyInAnyOrderElementsOf(expectedFiles);
    }
}
