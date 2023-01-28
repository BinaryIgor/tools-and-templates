package io.codyn.app.template.user.common.test;

import io.codyn.app.template._common.app.EmailModuleProvider;
import io.codyn.app.template.user.common.core.UserEmailComponent;
import io.codyn.email.factory.EmailFactory;
import io.codyn.email.model.EmailAddress;
import io.codyn.email.server.EmailServer;

public class TestUserEmailComponentProvider {

    public static final UserEmailComponent.Config CONFIG = new UserEmailComponent.Config("https://best-app.com",
            EmailAddress.ofNameEmail("Admin", "app@codyn.io"),
            "sign-in",
            "sign-up",
            "user-account",
            "forgot-password",
            "new-password");

    public static UserEmailComponent component(EmailFactory factory, EmailServer server) {
        return new UserEmailComponent(factory, server, CONFIG);
    }

    public static UserEmailComponent component(EmailServer server) {
        return component(EmailModuleProvider.factory(), server);
    }
}
