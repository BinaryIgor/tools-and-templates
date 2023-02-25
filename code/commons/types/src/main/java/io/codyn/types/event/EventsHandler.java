package io.codyn.types.event;

public interface EventsHandler {

    default String name() {
        return getClass().getSimpleName();
    }

    void subscribe();
}
