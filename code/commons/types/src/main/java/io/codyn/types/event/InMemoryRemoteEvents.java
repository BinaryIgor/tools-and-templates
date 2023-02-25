package io.codyn.types.event;

import java.util.function.Supplier;

public class InMemoryRemoteEvents implements RemoteEvents {

    private final InMemoryEvents topicEvents = new InMemoryEvents();
    private final InMemoryEvents queueEvents = new InMemoryEvents();
    private final Supplier<RemotePublisher> publisher = new Supplier<>() {

        private RemotePublisher publisher;

        @Override
        public RemotePublisher get() {
            if (publisher == null) {
                publisher = new RemotePublisher() {

                    @Override
                    public <T> void publish(Queue<T> queue, T data) {
                        queueEvents.publisher().publish(data);
                    }

                    @Override
                    public <T> void publish(Topic<T> topic, T data) {
                        topicEvents.publisher().publish(data);
                    }
                };
            }
            return publisher;
        }
    };

    @Override
    public <T> void subscribe(Topic<T> topic, Subscriber<T> subscriber) {
        topicEvents.subscribe(topic.dataType(), subscriber);
    }

    @Override
    public <T> void subscribe(Queue<T> queue, Subscriber<T> subscriber) {
        queueEvents.subscribe(queue.dataType(), subscriber);
    }

    @Override
    public RemotePublisher publisher() {
        return publisher.get();
    }
}
