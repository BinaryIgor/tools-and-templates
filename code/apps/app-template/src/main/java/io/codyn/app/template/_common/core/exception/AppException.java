package io.codyn.app.template._common.core.exception;

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

    public List<String> reasons() {
        return reasons;
    }

    public String toError() {
        return getClass().getSimpleName();
    }
}
