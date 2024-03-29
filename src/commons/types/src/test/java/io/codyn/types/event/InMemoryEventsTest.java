package io.codyn.types.event;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

public class InMemoryEventsTest {

    private InMemoryEvents events;

    @BeforeEach
    void setup() {
        events = new InMemoryEvents();
    }

    @Test
    void shouldCallAllSubscribersAndThrowExceptionWhilePublishingWithSinglePublisherException() {
        var firstData = new AtomicReference<TestEvent>();
        var secondData = new AtomicReference<TestEvent>();

        var exception = new RuntimeException("The only exception");

        events.subscribe(TestEvent.class, e -> {
            firstData.set(e);
            throw exception;
        });
        events.subscribe(TestEvent.class, secondData::set);

        var event = new TestEvent(22, "some event");

        Assertions.assertThatThrownBy(() -> events.publisher().publish(event))
                .isEqualTo(exception);

        Assertions.assertThat(firstData.get()).isEqualTo(event);
        Assertions.assertThat(secondData.get()).isEqualTo(event);
    }

    @Test
    void shouldCallAllSubscribersAndThrowCombinedException() {
        var firstData = new AtomicReference<String>();
        var secondData = new AtomicReference<String>();

        var firstException = new RuntimeException("First subscriber error");
        var secondException = new RuntimeException("Second subscriber error");

        events.subscribe(String.class, e -> {
            firstData.set(e);
            throw firstException;
        });
        events.subscribe(String.class, e -> {
            secondData.set(e);
            throw secondException;
        });

        var data = "Some data";

        Assertions.assertThatThrownBy(() -> events.publisher().publish(data))
                .hasSuppressedException(firstException)
                .hasSuppressedException(secondException);

        Assertions.assertThat(firstData.get()).isEqualTo(data);
        Assertions.assertThat(secondData.get()).isEqualTo(data);
    }

    private record TestEvent(long id, String name) {
    }
}
