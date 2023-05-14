package io.codyn.rabbitmq;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class RabbitmqConnectionFactory {

    private static final Logger log = LoggerFactory.getLogger(RabbitmqConnectionFactory.class);
    private static final int INITIAL_CONNECTION_TRIALS = 5;
    private static final long INITIAL_CONNECTION_FAILURE_INTERVAL = 250;
    private static final String PUBLISHER = "publisher";
    private static final String CONSUMER = "consumer";

    public static Connection newConnection(ConnectionParams params) {
        var connectionFactory = new ConnectionFactory();

        connectionFactory.setHost(params.host);
        connectionFactory.setPort(params.port);
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(params.recoveryInterval);
        connectionFactory.setRequestedHeartbeat(30);

        if (params.user != null && params.pass != null) {
            connectionFactory.setUsername(params.user);
            connectionFactory.setPassword(params.pass);
        }

        try {
            return retryingConnection(connectionFactory, params.name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection retryingConnection(ConnectionFactory connectionFactory,
                                                 String connectionName) throws Exception {
        var nextDelay = INITIAL_CONNECTION_FAILURE_INTERVAL;
        for (int i = 1; i <= INITIAL_CONNECTION_TRIALS; i++) {
            try {
                return connectionFactory.newConnection(connectionName);
            } catch (Exception e) {
                e.printStackTrace();
                if (i < INITIAL_CONNECTION_TRIALS) {
                    log.info("Can't connect to rabbitmq in {} trial, will try again in {} ms", i, nextDelay);
                    Thread.sleep(nextDelay);
                    nextDelay *= 2;
                } else {
                    throw new RuntimeException(
                            "Failed to connect to rabbitmq in %s trials".formatted(INITIAL_CONNECTION_TRIALS),
                            e);
                }
            }
        }

        throw new RuntimeException("0 rabbitmq connection trials");
    }

    public static void addRecoveryListener(Connection connection, Consumer<Boolean> listener) {
        addRecoveryListener(connection, listener, false);
    }

    public static void addRecoveryListener(Connection connection, Consumer<Boolean> listener, boolean publisher) {
        var connectionType = publisher ? PUBLISHER : CONSUMER;
        ((RecoverableConnection) connection).addRecoveryListener(new RecoveryListener() {
            @Override
            public void handleRecovery(Recoverable recoverable) {
                try {
                    log.info("Rabbitmq {} connection recovered!", connectionType);
                    listener.accept(true);
                } catch (Exception e) {
                    log.error("Problem while informing about recovered rabbitmq connection: "
                            + connectionType, e);
                }
            }

            @Override
            public void handleRecoveryStarted(Recoverable recoverable) {
                try {
                    log.warn("Starting rabbitmq {} connection recovery..", connectionType);
                    listener.accept(false);
                } catch (Exception e) {
                    log.error("Problem while informing about started rabbitmq connection recovery: "
                            + connectionType, e);
                }
            }
        });
    }

    public static void addOnRecoveredListener(Connection connection, Runnable listener) {
        addRecoveryListener(connection, conn -> {
            if (conn) {
                listener.run();
            }
        }, true);
    }

    public record ConnectionParams(String name,
                                   String host,
                                   int port,
                                   String user,
                                   String pass,
                                   int recoveryInterval) {

        public ConnectionParams(String name, String host, int port, String user, String pass) {
            this(name, host, port, user, pass, 3000);
        }

        public static ConnectionParams of(String name, boolean publisher,
                                          String host, int port,
                                          String user, String pass) {
            return new ConnectionParams("%s-%s".formatted(name, publisher ? PUBLISHER : CONSUMER),
                    host, port, user, pass);
        }
    }
}
