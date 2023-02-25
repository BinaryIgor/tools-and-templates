package io.codyn.types.event;

public interface RemoteEvents {

    <T> void subscribe(Topic<T> topic, Subscriber<T> subscriber);

    <T> void subscribe(Queue<T> queue, Subscriber<T> subscriber);

    RemotePublisher publisher();
}
