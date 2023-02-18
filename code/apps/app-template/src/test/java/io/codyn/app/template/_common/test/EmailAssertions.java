package io.codyn.app.template._common.test;

import io.codyn.email.model.Email;
import io.codyn.email.model.EmailAddress;
import org.assertj.core.api.Assertions;

public class EmailAssertions {

    public static void toIsEqualTo(Email email, EmailAddress to) {
        Assertions.assertThat(email.to()).isEqualTo(to);
    }

    public static void messageContains(Email email, String... texts) {
        Assertions.assertThat(email.textMessage()).contains(texts);
        Assertions.assertThat(email.htmlMessage()).contains(texts);
    }
}
