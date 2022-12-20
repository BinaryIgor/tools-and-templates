package io.codyn.app.template._shared.domain.exception;

import java.util.List;

public class AppException extends RuntimeException {

    private final List<String> reasons;

    public AppException(String message, List<String> reasons) {
        super(message);
        this.reasons = reasons;
    }

    public AppException(String message, String... reasons) {
        this(message, List.of(reasons));
    }

    public List<String> reasons() {
        return reasons;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AppException e) {
            return getClass().equals(e.getClass())
                    && getMessage().equals(e.getMessage())
                    && reasons.equals(e.reasons);
        }

        return false;
    }
}
