package io.codyn.app.template._shared.test;

import io.codyn.types.EventPublisher;

public class TestEventPublisher implements EventPublisher {

    public Object publishedEvent;

    @Override
    public <T> void publish(T event) {
        publishedEvent = event;
    }
}
