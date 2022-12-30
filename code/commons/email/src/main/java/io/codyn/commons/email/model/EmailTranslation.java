package io.codyn.commons.email.model;

import java.util.Map;

public record  EmailTranslation(String subject,
                               Map<String, String> messages) {
}
