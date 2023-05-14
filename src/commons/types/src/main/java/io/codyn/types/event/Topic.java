package io.codyn.types.event;

public record Topic<T>(String name, Class<T> dataType) {
}
