package io.codyn.app.template._common.core.exception;

public class InvalidEmailException extends AppException {

    public InvalidEmailException(String email) {
        super("%s is not a valid email".formatted(email));
    }
}
