package io.codyn.test.event;

import io.codyn.types.event.*;
import org.junit.jupiter.api.BeforeEach;

public abstract class EventsHandlerTest {

    protected final LocalEvents localEvents = new InMemoryEvents();
    protected final LocalPublisher localPublisher = localEvents.publisher();
    protected final RemoteEvents remoteEvents = new InMemoryRemoteEvents();
    protected final RemotePublisher remotePublisher = remoteEvents.publisher();

    protected abstract void setup(Events events);

    @BeforeEach
    void setup() {
        setup(new Events(localEvents, remoteEvents));
    }
}
