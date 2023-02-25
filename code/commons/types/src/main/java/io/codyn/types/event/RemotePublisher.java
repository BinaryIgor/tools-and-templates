package io.codyn.types.event;

public interface RemotePublisher {
    <T> void publish(RemoteTopic<T> topic, T data);
}
