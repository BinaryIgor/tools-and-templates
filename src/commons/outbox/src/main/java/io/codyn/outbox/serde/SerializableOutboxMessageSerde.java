package io.codyn.outbox.serde;

import io.codyn.outbox.OutboxMessage;

import java.io.*;

public class SerializableOutboxMessageSerde implements OutboxMessageSerde {

    @Override
    public <T> byte[] serialize(T message) {
        if (!(message instanceof Serializable)) {
            throw new RuntimeException("Message must be serializable, but %s is not!".formatted(message.getClass()));
        }

        try (var baos = new ByteArrayOutputStream();
             var os = new ObjectOutputStream(baos)) {
            os.writeObject(message);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error while serializing %s!".formatted(message.getClass()), e);
        }
    }

    @Override
    public Object deserialize(OutboxMessage message) {
        try (var bais = new ByteArrayInputStream(message.data());
             var is = new ObjectInputStream(bais)) {
            return is.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Problems while reading %s message of %s type...."
                    .formatted(message.id(), message.type()), e);
        }
    }
}
