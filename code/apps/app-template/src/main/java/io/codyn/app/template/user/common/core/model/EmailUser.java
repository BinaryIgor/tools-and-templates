package io.codyn.app.template.user.common.core.model;

import io.codyn.app.template._common.core.model.ApplicationLanguage;

public record EmailUser(String name,
                        String email,
                        ApplicationLanguage language) {

    public EmailUser(String name, String email) {
        this(name, email, ApplicationLanguage.EN);
    }
}
