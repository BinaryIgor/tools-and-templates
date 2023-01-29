package io.codyn.app.template.user.auth.core.event;

import io.codyn.app.template.user.api.event.UserCreatedEvent;
import io.codyn.app.template.user.common.core.ActivationTokens;
import io.codyn.app.template.user.common.core.UserEmailSender;
import io.codyn.app.template.user.common.core.model.EmailUser;
import io.codyn.types.EventHandler;
import org.springframework.stereotype.Component;


@Component
public class UserCreatedEventHandler implements EventHandler<UserCreatedEvent> {


    private final ActivationTokens activationTokens;
    private final UserEmailSender emailSender;

    public UserCreatedEventHandler(ActivationTokens activationTokens,
                                   UserEmailSender emailSender) {
        this.activationTokens = activationTokens;
        this.emailSender = emailSender;
    }

    @Override
    public void handle(UserCreatedEvent event) {
        var activationToken = activationTokens.saveNewUser(event.id());

        emailSender.sendAccountActivation(new EmailUser(event.name(), event.email()),
                activationToken.token());
    }
}
