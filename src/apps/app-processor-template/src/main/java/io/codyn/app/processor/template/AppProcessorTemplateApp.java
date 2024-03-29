package io.codyn.app.processor.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.codyn.app.processor.template")
public class AppProcessorTemplateApp {
    public static void main(String[] args) {
        SpringApplication.run(AppProcessorTemplateApp.class, args);
    }
}
