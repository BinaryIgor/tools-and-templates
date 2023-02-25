package io.codyn.rabbitmq;

import io.codyn.json.JsonMapper;
import io.codyn.rabbitmq.test.TestChannel;
import io.codyn.rabbitmq.test.TestConnection;
import io.codyn.test.TestRandom;
import io.codyn.types.Pair;
import io.codyn.types.event.Queue;
import io.codyn.types.event.Topic;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.RabbitMQContainer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RabbitmqPublisherTest {

    private static final Topic<Data> TOPIC = new Topic<>("/data-pub-sub-topic", Data.class);
    private static final Queue<Data> QUEUE = new Queue<>("/data-queue-topic", Data.class);

    @Test
    @Tag("integration")
    void shouldSaveFailedMessagesForResending() {
        var rabbitMq = RabbitMQContainerDefinition.container();
        try {
            rabbitMq.start();

            var connection = RabbitmqConnectionFactory.newConnection(newConnectionParams(rabbitMq));

            var publisher = new RabbitmqPublisher(connection);
            var data = new Data(1, "some text");
            publisher.publish(TOPIC, data);

            rabbitMq.stop();

            Assertions.assertThat(publisher.toResend()).isZero();

            publisher.publish(TOPIC, data);

            Assertions.assertThat(publisher.toResend()).isOne();
        } finally {
            rabbitMq.stop();
        }
    }

    @Test
    @Tag("integration")
    void shouldNotSaveMessageForResendingForQueueMessages() {
        var rabbitMq = RabbitMQContainerDefinition.container();
        try {
            rabbitMq.start();

            var connection = RabbitmqConnectionFactory.newConnection(newConnectionParams(rabbitMq));

            var publisher = new RabbitmqPublisher(connection);
            var data = new Data(TestRandom.longValue(), TestRandom.string());

            publisher.publish(QUEUE, data);

            rabbitMq.stop();

            Assertions.assertThat(publisher.toResend()).isZero();

            Assertions.assertThatThrownBy(() -> publisher.publish(QUEUE, data))
                    .isInstanceOf(RuntimeException.class);

            Assertions.assertThat(publisher.toResend()).isZero();
        } finally {
            rabbitMq.stop();
        }
    }

    @Test
    void shouldCacheAndResendLastNEventsAfterRecovery() {
        var failedLimit = 5;
        var testChannel = new TestChannel();
        var publisher = new RabbitmqPublisher(new TestConnection(testChannel), failedLimit, 10_000);

        testChannel.publishException = new RuntimeException("Can't publish");

        var testData = prepareRepublishTestData(failedLimit);

        testData.toPublish().forEach(p -> publisher.publish(p.first(), p.second()));

        Assertions.assertThat(testChannel.publishedArgs).isEqualTo(testData.published());

        testChannel.clearPublished();
        testChannel.publishException = null;

        publisher.onRecovered();

        Assertions.assertThat(testChannel.publishedArgs).isEqualTo(testData.republished());
    }

    private RabbitmqConnectionFactory.ConnectionParams newConnectionParams(RabbitMQContainer rabbitMq) {
        return new RabbitmqConnectionFactory.ConnectionParams("Test",
                rabbitMq.getHost(),
                rabbitMq.getAmqpPort(),
                RabbitMQContainerDefinition.USER,
                RabbitMQContainerDefinition.PASS);
    }

    private RepublishTestData prepareRepublishTestData(int failedLimit) {
        var toPublish = Stream.generate(() ->
                        new Pair<>(TOPIC, new Data(TestRandom.longValue(), TestRandom.name())))
                .limit(failedLimit * 2L)
                .collect(Collectors.toList());

        var published = toPublish.stream()
                .map(e -> new TestChannel.PublishedArgs(TOPIC.name(), "",
                        JsonMapper.json(e.second())))
                .toList();

        var republished = published.stream()
                .skip(toPublish.size() - failedLimit)
                .collect(Collectors.toList());

        return new RepublishTestData(toPublish, published, republished);
    }

    private record Data(long id, String value) {
    }

    private record RepublishTestData(List<Pair<Topic<Data>, Data>> toPublish,
                                     List<TestChannel.PublishedArgs> published,
                                     List<TestChannel.PublishedArgs> republished) {
    }
}
