package io.codyn.types.event;

import java.util.function.Supplier;

public class InMemoryRemoteEvents implements RemoteEvents {

    private final InMemoryEvents events = new InMemoryEvents();
    private final Supplier<RemotePublisher> publisher = new Supplier<>() {

        private RemotePublisher publisher;

        @Override
        public RemotePublisher get() {
            if (publisher == null) {
                publisher = new RemotePublisher() {
                    @Override
                    public <T> void publish(Topic<T> topic, T data) {
                        events.publisher().publish(data);
                    }
                };
            }
            return publisher;
        }
    };

    @Override
    public <T> void subscribe(Topic<T> topic, Subscriber<T> subscriber) {
        events.subscribe(topic.dataType(), subscriber);
    }

    @Override
    public RemotePublisher publisher() {
        return publisher.get();
    }
}
