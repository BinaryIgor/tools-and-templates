package io.codyn.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;


public class ScopedChannels {

    private static final Logger log = LoggerFactory.getLogger(ScopedChannels.class);
    private final Connection connection;
    private final ThreadLocal<Channel> scoped = new ThreadLocal<>();

    public ScopedChannels(Connection connection) {
        this.connection = connection;
    }

    public void use(ThrowingConsumer<Channel> user, Supplier<Boolean> onFailure) {
        try {
            var channel = channel();
            user.accept(channel);
        } catch (Exception e) {
            close();
            var swallow = false;
            if (onFailure != null) {
                swallow = onFailure.get();
            }
            if (swallow) {
                log.error("Problem while using rabbitmq channel...", e);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public void use(ThrowingConsumer<Channel> user) {
        use(user, null);
    }

    private Channel channel() throws Exception {
        var channel = scoped.get();
        if (channel == null || !channel.isOpen()) {
            channel = connection.createChannel();
            scoped.set(channel);
        }
        return channel;
    }

    private void close() {
        try {
            var channel = scoped.get();
            if (channel != null) {
                scoped.remove();
                channel.close();
            }
        } catch (Exception ignored) {

        }
    }

    public interface ThrowingConsumer<T> {
        void accept(T item) throws Exception;
    }
}
