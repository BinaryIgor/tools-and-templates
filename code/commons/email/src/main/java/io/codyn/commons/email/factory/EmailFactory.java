package io.codyn.commons.email.factory;

import io.codyn.commons.email.model.Email;
import io.codyn.commons.email.model.NewEmailTemplate;

public interface EmailFactory {
    Email newEmail(NewEmailTemplate template);
}
