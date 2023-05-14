package io.codyn.types.event;

public record Queue<T>(String name, Class<T> dataType) {
}
