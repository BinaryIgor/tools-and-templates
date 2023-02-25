package io.codyn.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Delivery;
import io.codyn.json.JsonMapper;
import io.codyn.types.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class RabbitmqEvents implements RemoteEvents {

    private static final Logger log = LoggerFactory.getLogger(RabbitmqEvents.class);
    private final List<Consumer<Boolean>> connectionStateListeners = new ArrayList<>();
    private final ScopedChannels consumerChannels;
    private final RemotePublisher publisher;
    private final PubSubConfig pubSubConfig;

    public RabbitmqEvents(Connection publisherConnection,
                          Connection consumerConnection,
                          Function<RemotePublisher, RemotePublisher> publisherDecorator,
                          PubSubConfig pubSubConfig) {
        consumerChannels = new ScopedChannels(consumerConnection);

        RabbitmqConnectionFactory.addRecoveryListener(consumerConnection,
                conn -> connectionStateListeners.forEach(l -> l.accept(conn)));

        var basePublisher = new RabbitmqPublisher(publisherConnection);
        RabbitmqConnectionFactory.addOnRecoveredListener(publisherConnection, basePublisher::onRecovered);

        publisher = publisherDecorator.apply(basePublisher);

        this.pubSubConfig = pubSubConfig;
    }

    public RabbitmqEvents(Connection publisherConnection, Connection consumerConnection, PubSubConfig pubSubConfig) {
        this(publisherConnection, consumerConnection, Function.identity(), pubSubConfig);
    }

    @Override
    public RemotePublisher publisher() {
        return publisher;
    }

    @Override
    public <T> void subscribe(Topic<T> topic, Subscriber<T> subscriber) {
        consumerChannels.use(channel -> {
            var queue = setupTopicQueue(channel, topic);
            consumeFromChannel(channel, queue, topic.name(), topic.dataType(), subscriber);
        });
    }

    private <T> void consumeFromChannel(Channel channel,
                                        String preparedQueue,
                                        String topicOrQueue,
                                        Class<T> dataType,
                                        Subscriber<T> subscriber) throws Exception {
        channel.basicConsume(preparedQueue, false, (consumerTag, delivery) -> {
            var message = messageOrEmpty(topicOrQueue, delivery);
            try {
                var data = JsonMapper.object(message, dataType);

                subscriber.onEvent(data);

                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (Exception e) {
                log.error("Failure while consuming from topic/queue: {}, message: {}, exception:", topicOrQueue,
                        message, e);
            }
        }, consumerTag -> {
        });
    }


    @Override
    public <T> void subscribe(Queue<T> queue, Subscriber<T> subscriber) {
        consumerChannels.use(channel -> {
            consumeFromChannel(channel, setupQueue(channel, queue),
                    queue.name(), queue.dataType(), subscriber);
        });
    }

    private String messageOrEmpty(String topicOrQueue, Delivery delivery) {
        try {
            return new String(delivery.getBody(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Problem while parsing message body of topic/queue: {}, exception:", topicOrQueue, e);
            return "";
        }
    }

    private <T> String setupQueue(Channel channel, Queue<T> queue) throws Exception {
        channel.queueDeclare(queue.name(), true, false, false, null);
        return queue.name();
    }


    private <T> String setupTopicQueue(Channel channel, Topic<T> topic) throws Exception {
        var exchange = topic.name();

        channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT, true);

        var queue = exchange + "_" + pubSubConfig.subscriberId;

        var declaration = channel.queueDeclare(queue, true, false, false,
                Map.of("x-message-ttl", pubSubConfig.messagesTtl));

        if (declaration.getConsumerCount() > 0) {
            throw new RuntimeException("%s exchange queue should have exactly one subscriber".formatted(queue));
        }

        channel.queueBind(queue, exchange, "");

        return queue;
    }

    public void addConnectionStateListener(Consumer<Boolean> listener) {
        connectionStateListeners.add(listener);
    }

    public record PubSubConfig(String subscriberId, long messagesTtl) {

        public static PubSubConfig withDefaultTtl(String subscriberId) {
            return new PubSubConfig(subscriberId, TimeUnit.HOURS.toSeconds(1));
        }
    }
}
