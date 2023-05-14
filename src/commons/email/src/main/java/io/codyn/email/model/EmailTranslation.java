package io.codyn.email.model;

import java.util.Map;

public record  EmailTranslation(String subject,
                               Map<String, String> messages) {
}
