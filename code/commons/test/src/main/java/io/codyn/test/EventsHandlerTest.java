package io.codyn.test;

import io.codyn.types.event.*;
import org.junit.jupiter.api.BeforeEach;

public abstract class EventsHandlerTest {

    protected final LocalEvents localEvents = new InMemoryEvents();
    protected final LocalPublisher localPublisher = localEvents.publisher();
    protected final RemoteEvents remoteEvents = new InMemoryRemoteEvents();
    protected final RemotePublisher remotePublisher = remoteEvents.publisher();

    protected abstract EventsHandler newHandler(Events events);

    @BeforeEach
    void setup() {
        var handler = newHandler(new Events(localEvents, remoteEvents));
        handler.subscribe();
    }
}
