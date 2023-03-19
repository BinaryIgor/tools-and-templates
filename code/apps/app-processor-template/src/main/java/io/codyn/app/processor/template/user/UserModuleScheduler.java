package io.codyn.app.processor.template.user;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserModuleScheduler {

    @Scheduled(fixedDelayString = "PT1S")
    public void deleteNotActivatedUsers() {
        System.out.println("Should delete them users...");
    }
}
