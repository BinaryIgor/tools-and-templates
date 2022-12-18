package io.codyn.app.template._shared.domain;

public interface EventPublisher {
    <T> void publish(T event);
}
