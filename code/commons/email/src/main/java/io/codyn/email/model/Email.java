package io.codyn.email.model;

public record Email(EmailAddress from,
                    EmailAddress to,
                    String subject,
                    String htmlMessage,
                    String textMessage) {

}
