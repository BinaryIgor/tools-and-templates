package io.codyn.test.event;

import io.codyn.types.event.Queue;
import io.codyn.types.event.RemoteEvents;
import io.codyn.types.event.Topic;

import java.util.ArrayList;

public class TestRemoteEvents {

    public static <T> EventsCaptor<T> subscribe(RemoteEvents events, Topic<T> topic) {
        var captured = new ArrayList<T>();
        events.subscribe(topic, captured::add);
        return new EventsCaptor<>(captured);
    }

    public static <T> EventsCaptor<T> subscribe(RemoteEvents events, Queue<T> queue) {
        var captured = new ArrayList<T>();
        events.subscribe(queue, captured::add);
        return new EventsCaptor<>(captured);
    }
}
