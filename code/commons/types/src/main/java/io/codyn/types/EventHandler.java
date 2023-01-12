package io.codyn.types;

public interface EventHandler<T> {
    void handle(T event);
}
