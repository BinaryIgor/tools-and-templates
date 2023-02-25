package io.codyn.types.event;

public record RemoteTopic<T>(String value,
                             Class<T> dataType,
                             RemoteTopicType topicType) {

    public static <T> RemoteTopic<T> pubSub(String value, Class<T> dataType) {
        return new RemoteTopic<>(value, dataType, RemoteTopicType.PUB_SUB);
    }

    public static <T> RemoteTopic<T> queue(String value, Class<T> dataType) {
        return new RemoteTopic<>(value, dataType, RemoteTopicType.QUEUE);
    }
}
