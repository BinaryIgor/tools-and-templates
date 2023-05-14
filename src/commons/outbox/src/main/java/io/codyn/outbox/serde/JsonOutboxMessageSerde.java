package io.codyn.outbox.serde;

import io.codyn.json.JsonMapper;
import io.codyn.outbox.OutboxMessage;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonOutboxMessageSerde implements OutboxMessageSerde {

    private final Map<String, Class<?>> typesMapping;
    private final Map<Class<?>, String> reversedTypesMapping;

    public JsonOutboxMessageSerde(Map<String, Class<?>> typesMapping) {
        this.typesMapping = typesMapping;
        this.reversedTypesMapping = typesMapping.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    @Override
    public <T> String messageType(T message) {
        return Optional.ofNullable(reversedTypesMapping.get(message.getClass()))
                .orElseThrow(() -> new RuntimeException("There is no mapping for %s class"
                        .formatted(message.getClass())));
    }

    @Override
    public <T> byte[] serialize(T message) {
        return JsonMapper.json(message).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Object deserialize(OutboxMessage message) {
        var classType = typesMapping.get(message.type());
        if (classType == null) {
            throw new RuntimeException("There is no mapping for %s type".formatted(message.type()));
        }

        try {
            var json = new String(message.data(), StandardCharsets.UTF_8);
            return JsonMapper.object(json, classType);
        } catch (Exception e) {
            throw new RuntimeException("Problems while deserializing %s message of %s type for %s as json"
                    .formatted(message.id(), message.type(), message.target()), e);
        }
    }
}
