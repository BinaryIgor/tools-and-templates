package io.codyn.types.event;

public interface RemoteEvents {

    <T> void subscribe(Topic<T> topic, Subscriber<T> subscriber);

    RemotePublisher publisher();
}
