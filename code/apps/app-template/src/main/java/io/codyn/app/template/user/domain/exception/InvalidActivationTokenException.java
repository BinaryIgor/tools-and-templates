package io.codyn.app.template.user.domain.exception;

import io.codyn.app.template._shared.domain.exception.AppException;
import io.codyn.app.template.user.domain.model.activation.ActivationTokenId;

public class InvalidActivationTokenException extends AppException {

    public InvalidActivationTokenException(String message) {
        super(message);
    }

    public static InvalidActivationTokenException ofToken(String token) {
        return new InvalidActivationTokenException("%s is not a valid activation token".formatted(token));
    }

    public static InvalidActivationTokenException ofToken(ActivationTokenId id, String message) {
        return new InvalidActivationTokenException("%s: %s".formatted(id, message));
    }
}
