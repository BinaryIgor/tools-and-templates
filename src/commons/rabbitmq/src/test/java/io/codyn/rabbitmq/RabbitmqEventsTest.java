package io.codyn.rabbitmq;

import com.rabbitmq.client.Connection;
import io.codyn.test.TestAsync;
import io.codyn.test.TestRandom;
import io.codyn.types.event.Queue;
import io.codyn.types.event.Topic;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.RabbitMQContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tag("integration")
public class RabbitmqEventsTest {

    private static final RabbitMQContainer RABBIT_MQ = RabbitMQContainerDefinition.container();
    private RabbitmqEvents events;
    private Connection consumerConnection;
    private String subscriberId;
    private long pubSubMessagesTtl;

    @BeforeAll
    static void allSetup() {
        RABBIT_MQ.start();
    }

    @AfterAll
    static void allTeardown() {
        RABBIT_MQ.stop();
    }

    @BeforeEach
    void setup() {
        var publisherConnection = newConnection();
        consumerConnection = newConnection();
        subscriberId = TestRandom.id();
        pubSubMessagesTtl = TestRandom.inRange(10_000, 100_000);

        events = new RabbitmqEvents(publisherConnection, consumerConnection,
                new RabbitmqEvents.PubSubConfig(subscriberId, pubSubMessagesTtl));
    }

    @Test
    void shouldThrowExceptionGivenDuplicatedPubSubTopicSubscriber() {
        var topic = new Topic<>("/pub-sub", Data.class);

        events.subscribe(topic, d -> {
        });

        Assertions.assertThatThrownBy(
                        () -> events.subscribe(topic, d -> {

                        }))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("%s exchange queue should have exactly one subscriber"
                        .formatted(topicQueue(topic.name(), subscriberId)));

    }

    @Test
    void shouldSubscribeToTopic() {
        var secondSubscriberId = TestRandom.id();
        var secondEvents = new RabbitmqEvents(newConnection(), consumerConnection,
                new RabbitmqEvents.PubSubConfig(secondSubscriberId, pubSubMessagesTtl));

        var topic = new Topic<>("/pub-sub", Data.class);

        var expectedData = preparedData();
        var latch = TestAsync.newLatch(2 * expectedData.size());

        var firstSubscriberData = new ArrayList<Data>();
        var secondSubscriberData = new ArrayList<Data>();

        var publisher = events.publisher();

        events.subscribe(topic, d -> {
            firstSubscriberData.add(d);
            latch.countDown();
        });
        secondEvents.subscribe(topic, d -> {
            secondSubscriberData.add(d);
            latch.countDown();
        });

        expectedData.forEach(d -> publisher.publish(topic, d));

        TestAsync.waitForCountDown(latch);

        Assertions.assertThat(firstSubscriberData).isEqualTo(expectedData);
        Assertions.assertThat(secondSubscriberData).isEqualTo(expectedData);

        assertPersistentQueueIsEmpty(topicQueue(topic.name(), subscriberId),
                Map.of("x-message-ttl", pubSubMessagesTtl));
        assertPersistentQueueIsEmpty(topicQueue(topic.name(), secondSubscriberId),
                Map.of("x-message-ttl", pubSubMessagesTtl));
    }

    @Test
    void shouldSubscribeToQueue() {
        var queue = new Queue<>("/queue", Data.class);

        var expectedData = preparedData();
        var latch = TestAsync.newLatch(expectedData.size());

        var firstSubscriberData = new ArrayList<Data>();
        var secondSubscriberData = new ArrayList<Data>();

        var publisher = events.publisher();

        events.subscribe(queue, d -> {
            firstSubscriberData.add(d);
            latch.countDown();
        });
        events.subscribe(queue, d -> {
            secondSubscriberData.add(d);
            latch.countDown();
        });

        expectedData.forEach(d -> publisher.publish(queue, d));

        TestAsync.waitForCountDown(latch);

        Assertions.assertThat(firstSubscriberData).isNotEmpty();
        Assertions.assertThat(secondSubscriberData).isNotEmpty();

        var subscribersData = new ArrayList<>(firstSubscriberData);
        subscribersData.addAll(secondSubscriberData);

        Assertions.assertThat(subscribersData)
                .containsExactlyInAnyOrderElementsOf(expectedData);

        assertPersistentQueueIsEmpty(queue.name());
    }

    private String topicQueue(String topic, String subscriberId) {
        return topic + "_" + subscriberId;
    }

    private void assertPersistentQueueIsEmpty(String queue) {
        assertPersistentQueueIsEmpty(queue, null);
    }

    private void assertPersistentQueueIsEmpty(String queue, Map<String, Object> arguments) {
        try {
            var channel = consumerConnection.createChannel();
            var declared = channel.queueDeclare(queue, true, false, false, arguments);
            Assertions.assertThat(declared.getMessageCount()).isZero();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Connection newConnection() {
        return RabbitmqConnectionFactory.newConnection(
                new RabbitmqConnectionFactory.ConnectionParams(
                        "Test",
                        RABBIT_MQ.getHost(),
                        RABBIT_MQ.getAmqpPort(),
                        RabbitMQContainerDefinition.USER,
                        RabbitMQContainerDefinition.PASS));
    }

    private List<Data> preparedData() {
        return Stream.generate(this::preparedSingleData)
                .limit(5)
                .collect(Collectors.toList());
    }

    private Data preparedSingleData() {
        return new Data(TestRandom.longValue(), TestRandom.string());
    }

    private record Data(long id, String name) {
    }
}
