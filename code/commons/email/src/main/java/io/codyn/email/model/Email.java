package io.codyn.email.model;

import java.util.Map;

public record Email(EmailAddress from,
                    EmailAddress to,
                    String subject,
                    String htmlMessage,
                    String textMessage,
                    Map<String, String> metadata) {

}
