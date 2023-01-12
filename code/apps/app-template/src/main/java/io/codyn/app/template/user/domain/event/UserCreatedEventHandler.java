package io.codyn.app.template.user.domain.event;

import io.codyn.app.template.user.api.event.UserCreatedEvent;
import io.codyn.app.template.user.domain.ActivationTokenFactory;
import io.codyn.app.template.user.domain.UserEmailComponent;
import io.codyn.app.template.user.domain.model.EmailUser;
import io.codyn.app.template.user.domain.repository.ActivationTokenRepository;
import io.codyn.types.EventHandler;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class UserCreatedEventHandler implements EventHandler<UserCreatedEvent> {

    private final UserEmailComponent emailComponent;
    private final ActivationTokenRepository activationTokenRepository;
    private final Clock clock;

    public UserCreatedEventHandler(UserEmailComponent emailComponent,
                                   ActivationTokenRepository activationTokenRepository,
                                   Clock clock) {
        this.emailComponent = emailComponent;
        this.activationTokenRepository = activationTokenRepository;
        this.clock = clock;
    }

    @Override
    public void handle(UserCreatedEvent event) {
        var activationToken = ActivationTokenFactory.newUser(event.id(), clock);

        activationTokenRepository.save(activationToken);

        emailComponent.sendAccountActivation(new EmailUser(event.name(), event.email()),
                activationToken.token());
    }
}
