package io.codyn.commons.types;

public interface EventHandler<T> {
    void handle(T event);
}
