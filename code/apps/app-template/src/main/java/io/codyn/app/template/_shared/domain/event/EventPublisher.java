package io.codyn.app.template._shared.domain.event;

public interface EventPublisher {
    <T> void publish(T event);
}
