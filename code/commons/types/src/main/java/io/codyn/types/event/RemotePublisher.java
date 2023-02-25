package io.codyn.types.event;

public interface RemotePublisher {

    <T> void publish(Queue<T> queue, T data);

    <T> void publish(Topic<T> topic, T data);
}
