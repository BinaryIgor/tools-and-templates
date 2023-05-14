package io.codyn.app.template;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.codyn.app.template")
public class AppTemplateApp {
    public static void main(String[] args) {
        SpringApplication.run(AppTemplateApp.class, args);
    }
}
