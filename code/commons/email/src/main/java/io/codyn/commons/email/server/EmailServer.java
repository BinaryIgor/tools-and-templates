package io.codyn.commons.email.server;

import io.codyn.commons.email.model.Email;

import java.util.Collection;

public interface EmailServer {

    void send(Email email);

    void sendBatch(Collection<Email> emails);

    class EmailException extends RuntimeException {

        public EmailException(Exception exception) {
            super(exception);
        }

        public EmailException(String message) {
            super(message);
        }
    }
}
