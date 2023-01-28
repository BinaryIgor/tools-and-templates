package io.codyn.app.template._common.test;

import io.codyn.types.EventHandler;

public class TestEventHandler<T> implements EventHandler<T> {

    public T handledEvent;

    @Override
    public void handle(T event) {
        handledEvent = event;
    }
}
