package io.codyn.commons.email.model;

import java.util.Map;

public record NewEmailTemplate(EmailAddress from,
                               EmailAddress to,
                               String language,
                               String template,
                               Map<String, String> variables) {
}
