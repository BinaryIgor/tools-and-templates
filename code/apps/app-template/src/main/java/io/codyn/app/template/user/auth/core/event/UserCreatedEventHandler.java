package io.codyn.app.template.user.auth.core.event;

import io.codyn.app.template.user.api.event.UserCreatedEvent;
import io.codyn.app.template.user.common.core.ActivationTokenFactory;
import io.codyn.app.template.user.common.core.ActivationTokenRepository;
import io.codyn.app.template.user.common.core.UserEmailComponent;
import io.codyn.app.template.user.common.core.model.EmailUser;
import io.codyn.types.EventHandler;
import org.springframework.stereotype.Component;


@Component
public class UserCreatedEventHandler implements EventHandler<UserCreatedEvent> {

    private final UserEmailComponent emailComponent;
    private final ActivationTokenRepository activationTokenRepository;
    private final ActivationTokenFactory activationTokenFactory;

    public UserCreatedEventHandler(UserEmailComponent emailComponent,
                                   ActivationTokenRepository activationTokenRepository,
                                   ActivationTokenFactory activationTokenFactory) {
        this.emailComponent = emailComponent;
        this.activationTokenRepository = activationTokenRepository;
        this.activationTokenFactory = activationTokenFactory;
    }

    @Override
    public void handle(UserCreatedEvent event) {
        var activationToken = activationTokenFactory.newUser(event.id());

        activationTokenRepository.save(activationToken);

        emailComponent.sendAccountActivation(new EmailUser(event.name(), event.email()),
                activationToken.token());
    }
}
