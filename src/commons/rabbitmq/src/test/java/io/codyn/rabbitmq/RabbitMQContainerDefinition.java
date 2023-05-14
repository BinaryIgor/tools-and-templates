package io.codyn.rabbitmq;

import org.testcontainers.containers.RabbitMQContainer;

public class RabbitMQContainerDefinition {

    public static final String USER = "guest";
    public static final String PASS = "codyn";

    public static RabbitMQContainer container() {
        return new RabbitMQContainer("rabbitmq:3.9.5").withAdminPassword(PASS);
    }
}
