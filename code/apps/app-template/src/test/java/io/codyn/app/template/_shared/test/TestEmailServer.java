package io.codyn.app.template._shared.test;

import io.codyn.email.model.Email;
import io.codyn.email.server.EmailServer;

import java.util.Collection;

public class TestEmailServer implements EmailServer {

    public Email sentEmail;
    public Collection<Email> sentEmailBatch;

    @Override
    public void send(Email email) {
        sentEmail = email;
    }

    @Override
    public void sendBatch(Collection<Email> emails) {
        sentEmailBatch = emails;
    }

    public void clear() {
        sentEmail = null;
        sentEmailBatch = null;
    }
}
