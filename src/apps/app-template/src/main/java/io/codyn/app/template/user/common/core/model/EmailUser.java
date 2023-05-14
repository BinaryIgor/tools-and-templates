package io.codyn.app.template.user.common.core.model;

import io.codyn.app.template._common.core.model.ApplicationLanguage;

import java.util.UUID;

public record EmailUser(UUID id,
                        String name,
                        String email,
                        ApplicationLanguage language) {

    public EmailUser(UUID id, String name, String email) {
        this(id, name, email, ApplicationLanguage.EN);
    }
}
