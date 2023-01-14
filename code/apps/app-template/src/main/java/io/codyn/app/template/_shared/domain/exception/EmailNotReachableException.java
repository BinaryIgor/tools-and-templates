package io.codyn.app.template._shared.domain.exception;

public class EmailNotReachableException extends CustomException {

    public EmailNotReachableException(String email) {
        super("%s email is not reachable".formatted(email));
    }
}
