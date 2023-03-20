package io.codyn.app.sockets.server.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.codyn.app.sockets.server.template")
public class AppSocketsServerTemplateApp {
    public static void main(String[] args) {
        SpringApplication.run(AppSocketsServerTemplateApp.class, args);
    }
}
