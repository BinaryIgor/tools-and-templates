package io.codyn.app.template.user.core.model;

import io.codyn.app.template._shared.core.model.ApplicationLanguage;

public record EmailUser(String name,
                        String email,
                        ApplicationLanguage language) {

    public EmailUser(String name, String email) {
        this(name, email, ApplicationLanguage.EN);
    }
}
