package io.codyn.app.template._shared.test;

import io.codyn.commons.types.EventHandler;

public class TestEventHandler<T> implements EventHandler<T> {

    public T handledEvent;

    @Override
    public void handle(T event) {
        handledEvent = event;
    }
}
