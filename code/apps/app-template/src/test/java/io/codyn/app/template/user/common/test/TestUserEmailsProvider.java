package io.codyn.app.template.user.common.test;

import io.codyn.app.template._common.app.EmailModuleProvider;
import io.codyn.app.template.user.common.core.UserEmailSender;
import io.codyn.email.factory.EmailFactory;
import io.codyn.email.model.EmailAddress;
import io.codyn.email.server.EmailServer;

public class TestUserEmailsProvider {

    public static final UserEmailSender.Config CONFIG = new UserEmailSender.Config("https://best-app.com",
            EmailAddress.ofNameEmail("Admin", "app@codyn.io"),
            "sign-in",
            "sign-up",
            "user-account",
            "forgot-password",
            "new-password");

    public static UserEmailSender sender(EmailFactory factory, EmailServer server) {
        return new UserEmailSender(factory, server, CONFIG);
    }

    public static UserEmailSender sender(EmailServer server) {
        return sender(EmailModuleProvider.factory(), server);
    }
}
