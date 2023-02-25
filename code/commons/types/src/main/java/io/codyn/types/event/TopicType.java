package io.codyn.types.event;

public enum TopicType {
    PUB_SUB, QUEUE;

    public boolean isPubSub() {
        return this == PUB_SUB;
    }
}
