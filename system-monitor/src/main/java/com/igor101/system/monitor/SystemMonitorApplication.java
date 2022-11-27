package com.igor101.system.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//TODO: check if prometheus & alertmanager are healthy
@SpringBootApplication
public class SystemMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemMonitorApplication.class, args);
    }
}
