package io.codyn.types.event;

public interface LocalEvents {

    <T> void subscribe(Class<T> event, Subscriber<T> subscriber);

    LocalPublisher publisher();
}
