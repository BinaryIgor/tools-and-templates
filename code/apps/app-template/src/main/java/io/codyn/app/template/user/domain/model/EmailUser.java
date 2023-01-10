package io.codyn.app.template.user.domain.model;

import io.codyn.app.template._shared.domain.model.ApplicationLanguage;

public record EmailUser(String name,
                        String email,
                        ApplicationLanguage language) {

    public EmailUser(String name, String email) {
        this(name, email, ApplicationLanguage.EN);
    }
}
