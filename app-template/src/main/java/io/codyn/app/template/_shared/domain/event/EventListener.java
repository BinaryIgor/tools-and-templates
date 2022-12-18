package io.codyn.app.template._shared.domain.event;

public interface EventListener<T> {
    void onEvent(T event);
}
