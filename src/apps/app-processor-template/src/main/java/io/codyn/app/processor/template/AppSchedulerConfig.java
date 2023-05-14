package io.codyn.app.processor.template;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Clock;

@Profile("!integration")
@EnableScheduling
@Configuration
public class AppSchedulerConfig {

    @Bean
    TaskScheduler taskScheduler(Clock clock) {
        var poolScheduler = new ThreadPoolTaskScheduler();
        poolScheduler.setClock(clock);
        poolScheduler.setPoolSize(5);
        poolScheduler.setWaitForTasksToCompleteOnShutdown(true);
        poolScheduler.setAwaitTerminationSeconds(30);
        return poolScheduler;
    }
}
