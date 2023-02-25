package io.codyn.test.event;

import io.codyn.types.event.LocalEvents;

import java.util.ArrayList;

public class TestLocalEvents {

    public static <T> EventsCaptor<T> subscribe(LocalEvents events, Class<T> event) {
        var captured = new ArrayList<T>();
        events.subscribe(event, captured::add);
        return new EventsCaptor<>(captured);
    }

    public static <T> EventsCaptor<T> subscribeThrowing(LocalEvents events,
                                                        Class<T> event,
                                                        RuntimeException exception) {
        var captured = new ArrayList<T>();
        events.subscribe(event, e -> {
            captured.add(e);
            throw exception;
        });
        return new EventsCaptor<>(captured);
    }
}
