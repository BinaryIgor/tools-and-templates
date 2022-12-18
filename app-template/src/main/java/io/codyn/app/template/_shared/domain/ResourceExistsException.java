package io.codyn.app.template._shared.domain;

import java.util.List;

public class ResourceExistsException extends RuntimeException {

    private final List<String> reasons;

    public ResourceExistsException(String message, String... reasons) {
        super(message);
        this.reasons = List.of(reasons);
    }

    public List<String> reasons() {
        return reasons;
    }
}
