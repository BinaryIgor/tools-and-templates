package io.codyn.types.event;

public interface RemoteEvents {

    <T> void subscribe(RemoteTopic<T> topic, Subscriber<T> subscriber);

    RemotePublisher publisher();
}
