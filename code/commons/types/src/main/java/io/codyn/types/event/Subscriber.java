package io.codyn.types.event;

public interface Subscriber<T> {
    void onEvent(T event);
}
