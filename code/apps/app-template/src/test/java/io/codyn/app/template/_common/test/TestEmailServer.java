package io.codyn.app.template._common.test;

import io.codyn.email.model.Email;
import io.codyn.email.server.EmailServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class TestEmailServer implements EmailServer {

    private static final Logger log = LoggerFactory.getLogger(TestEmailServer.class);
    public Email sentEmail;
    public Collection<Email> sentEmailBatch;

    @Override
    public void send(Email email) {
        sentEmail = email;
        log.info("Sending email...{}", email);
    }

    @Override
    public void sendBatch(Collection<Email> emails) {
        sentEmailBatch = emails;
    }

    public void clear() {
        sentEmail = null;
        sentEmailBatch = null;
    }

    public Email sendAndCaptureExpectedEmail(Runnable send) {
        send.run();
        var last = sentEmail;
        clear();
        return last;
    }

    public Collection<Email> sendAndCaptureExpectedEmailBatch(Runnable send) {
        send.run();
        var last = sentEmailBatch;
        clear();
        return last;
    }
}
