package io.codyn.app.template._shared.app;

import io.codyn.commons.types.EventPublisher;
import org.springframework.context.ApplicationEventPublisher;

public class SpringEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher publisher;

    public SpringEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public <T> void publish(T event) {
        publisher.publishEvent(event);
    }
}
