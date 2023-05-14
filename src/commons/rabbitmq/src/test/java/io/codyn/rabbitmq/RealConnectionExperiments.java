package io.codyn.rabbitmq;


import io.codyn.test.TestRandom;
import io.codyn.types.event.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RealConnectionExperiments {

    private static final Logger log = LoggerFactory.getLogger(RealConnectionExperiments.class);

    public static void main(String... args) {
        var publisherConnection = RabbitmqConnectionFactory.newConnection(
                new RabbitmqConnectionFactory.ConnectionParams(
                        "publisher", "localhost", 5672, "hairo", "hairo-rabbitmq"));
        var consumerConnection = RabbitmqConnectionFactory.newConnection(new RabbitmqConnectionFactory.ConnectionParams(
                "consumer", "localhost", 5672, "hairo", "hairo-rabbitmq"));

        var events = new RabbitmqEvents(publisherConnection, consumerConnection,
                RabbitmqEvents.PubSubConfig.withDefaultTtl("RealConnectionExperiments"));

        var topic = new Topic<>("/some-topic", Data.class);

        var publisher = events.publisher();

        events.subscribe(topic, d -> {
            log.info("Received data: " + d);
        });

        while (true) {
            try {
                Thread.sleep(3000);
                var data = new Data(TestRandom.longValue(), TestRandom.name());
                publisher.publish(topic, data);
                log.info("Data sent: " + data);
            } catch (Exception e) {
                log.error("Failed to sent data...", e);
            }
        }
    }

    record Data(long id, String value) {
    }
}
