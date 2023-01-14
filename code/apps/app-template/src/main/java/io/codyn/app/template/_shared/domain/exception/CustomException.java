package io.codyn.app.template._shared.domain.exception;

import java.util.List;

public class CustomException extends RuntimeException {

    private final List<String> reasons;

    public CustomException(String message, List<String> reasons) {
        super(message);
        this.reasons = reasons;
    }

    public CustomException(String message, String... reasons) {
        this(message, List.of(reasons));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CustomException e) {
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
