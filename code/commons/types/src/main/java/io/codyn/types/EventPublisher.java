package io.codyn.types;

public interface EventPublisher {
    <T> void publish(T event);
}
