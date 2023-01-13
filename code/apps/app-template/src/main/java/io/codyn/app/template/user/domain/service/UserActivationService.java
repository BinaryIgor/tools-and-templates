package io.codyn.app.template.user.domain.service;

import io.codyn.app.template.user.domain.component.ActivationTokenConsumer;
import io.codyn.app.template.user.domain.component.ActivationTokenData;
import io.codyn.app.template.user.domain.model.activation.ActivationTokenType;
import org.springframework.stereotype.Service;

@Service
public class UserActivationService {

    private final ActivationTokenConsumer activationTokenConsumer;

    public void activate(String activationToken) {
        activationTokenConsumer.consume(activationToken, ActivationTokenType.NEW_USER,
                userId -> {
                    //TODO: activate user
                });

        activationTokenConsumer.consumeWithData(activationToken, ActivationTokenType.NEW_EMAIL, data -> {
            var userId = data.

        });
    }
}
