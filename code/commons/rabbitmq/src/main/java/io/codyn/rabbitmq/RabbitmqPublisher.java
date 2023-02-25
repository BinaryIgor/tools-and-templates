package io.codyn.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import io.codyn.json.JsonMapper;
import io.codyn.types.event.RemotePublisher;
import io.codyn.types.event.Topic;

import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class RabbitmqPublisher implements RemotePublisher {

    static final int DEFAULT_FAILED_EVENTS_BACKUP_SIZE = 250;
    static final int DEFAULT_FAILED_EVENTS_TTL = 300_000;
    private final Queue<Event<?>> failedEvents = new ConcurrentLinkedQueue<>();
    private final ScopedChannels channels;
    private final int failedEventsBackupSize;
    private final int failedEventsTtl;

    public RabbitmqPublisher(ScopedChannels channels, int failedEventsBackupSize, int failedEventsTtl) {
        this.channels = channels;
        this.failedEventsBackupSize = failedEventsBackupSize;
        this.failedEventsTtl = failedEventsTtl;
    }

    public RabbitmqPublisher(ScopedChannels channels) {
        this(channels, DEFAULT_FAILED_EVENTS_BACKUP_SIZE, DEFAULT_FAILED_EVENTS_TTL);
    }

    public RabbitmqPublisher(Connection connection) {
        this(new ScopedChannels(connection), DEFAULT_FAILED_EVENTS_BACKUP_SIZE, DEFAULT_FAILED_EVENTS_TTL);
    }


    public RabbitmqPublisher(Connection connection, int failedEventsBackupSize, int failedEventsTtl) {
        this(new ScopedChannels(connection), failedEventsBackupSize, failedEventsTtl);
    }

    @Override
    public <T> void publish(io.codyn.types.event.Queue<T> queue, T data) {
        channels.use(channel -> {
            var message = jsonMessage(data);
            channel.basicPublish("", queue.name(), MessageProperties.PERSISTENT_TEXT_PLAIN, message);
        }, () -> false);
    }

    private byte[] jsonMessage(Object data) {
        return JsonMapper.json(data).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> void publish(Topic<T> topic, T data) {
        channels.use(channel -> {
            var message = jsonMessage(data);
            channel.basicPublish(topic.name(), "", MessageProperties.PERSISTENT_TEXT_PLAIN, message);
        }, () -> {
            if (failedEvents.size() >= failedEventsBackupSize) {
                failedEvents.poll();
            }
            failedEvents.add(new Event<>(topic, data, System.currentTimeMillis()));

            return true;
        });
    }

    public void onRecovered() {
        var now = System.currentTimeMillis();
        while (!failedEvents.isEmpty()) {
            var event = failedEvents.poll();

            var expired = now > (event.timestamp + failedEventsTtl);

            if (!expired) {
                publish(event);
            }
        }
    }

    private <T> void publish(Event<T> event) {
        publish(event.topic, event.data);
    }

    public int toResend() {
        return failedEvents.size();
    }

    private record Event<T>(Topic<T> topic, T data, long timestamp) implements Comparable<Event<?>> {

        @Override
        public int compareTo(Event<?> o) {
            return Long.compare(timestamp, o.timestamp);
        }
    }
}
