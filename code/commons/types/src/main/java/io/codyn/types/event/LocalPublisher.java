package io.codyn.types.event;

//Synchronous, local(in memory) publisher to simplify certain code flows.
public interface LocalPublisher {

    <T> void publish(T event);
}
