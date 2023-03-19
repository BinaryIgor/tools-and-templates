package io.codyn.app.template;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Profile("!integration")
@EnableScheduling
public class AppSchedulerConfig {
}
