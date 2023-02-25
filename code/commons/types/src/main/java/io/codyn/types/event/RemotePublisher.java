package io.codyn.types.event;

public interface RemotePublisher {
    <T> void publish(Topic<T> topic, T data);
}
