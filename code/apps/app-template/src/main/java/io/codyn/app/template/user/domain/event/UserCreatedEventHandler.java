package io.codyn.app.template.user.domain.event;

import io.codyn.app.template.user.api.event.UserCreatedEvent;
import io.codyn.app.template.user.domain.component.ActivationTokenFactory;
import io.codyn.app.template.user.domain.component.UserEmailComponent;
import io.codyn.app.template.user.domain.model.EmailUser;
import io.codyn.app.template.user.domain.repository.ActivationTokenRepository;
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
