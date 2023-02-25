package io.codyn.types.event;

public record Topic<T>(String value,
                       Class<T> dataType,
                       TopicType topicType) {

    public static <T> Topic<T> pubSub(String value, Class<T> dataType) {
        return new Topic<>(value, dataType, TopicType.PUB_SUB);
    }

    public static <T> Topic<T> queue(String value, Class<T> dataType) {
        return new Topic<>(value, dataType, TopicType.QUEUE);
    }
}
