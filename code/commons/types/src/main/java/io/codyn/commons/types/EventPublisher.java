package io.codyn.commons.types;

public interface EventPublisher {
    <T> void publish(T event);
}
