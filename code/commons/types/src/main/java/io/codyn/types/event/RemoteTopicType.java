package io.codyn.types.event;

public enum RemoteTopicType {
    PUB_SUB, QUEUE;

    public boolean isPubSub() {
        return this == PUB_SUB;
    }
}
