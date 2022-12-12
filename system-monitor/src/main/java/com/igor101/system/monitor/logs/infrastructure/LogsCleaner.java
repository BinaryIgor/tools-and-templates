package com.igor101.system.monitor.logs.infrastructure;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.regex.Pattern;

//TODO: test and implement
@EnableScheduling
public class LogsCleaner {

    private static final Pattern LOG_FILE_NAME_PATTERN = Pattern.compile("^(.+?__.+?).*");

    @Scheduled(fixedDelayString = "PT4H")
    void clear() {

    }
}
