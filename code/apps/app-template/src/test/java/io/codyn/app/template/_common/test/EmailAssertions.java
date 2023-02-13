package io.codyn.app.template._common.test;

import io.codyn.email.model.Email;
import org.assertj.core.api.Assertions;

public class EmailAssertions {

    public static void messageContains(Email email, String text) {
        Assertions.assertThat(email.textMessage()).contains(text);
        Assertions.assertThat(email.htmlMessage()).contains(text);
    }
}
