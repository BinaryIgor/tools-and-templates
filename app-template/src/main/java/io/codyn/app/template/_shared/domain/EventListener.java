package io.codyn.app.template._shared.domain;

public interface EventListener<T> {
    void onEvent(T event);
}
