package io.codyn.email.factory;

import io.codyn.email.model.Email;
import io.codyn.email.model.NewEmailTemplate;

public interface EmailFactory {
    Email newEmail(NewEmailTemplate template);
}
