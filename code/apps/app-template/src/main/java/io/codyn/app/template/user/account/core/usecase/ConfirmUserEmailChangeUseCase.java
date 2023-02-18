package io.codyn.app.template.user.account.core.usecase;

import io.codyn.app.template.user.account.core.model.ConfirmUserEmailChangeCommand;
import io.codyn.app.template.user.common.core.ActivationTokenConsumer;
import io.codyn.app.template.user.common.core.model.ActivationTokenType;
import io.codyn.app.template.user.common.core.repository.UserUpdateRepository;
import org.springframework.stereotype.Component;

@Component
public class ConfirmUserEmailChangeUseCase {

    private final ActivationTokenConsumer activationTokenConsumer;
    private final UserUpdateRepository userUpdateRepository;

    public ConfirmUserEmailChangeUseCase(ActivationTokenConsumer activationTokenConsumer,
                                         UserUpdateRepository userUpdateRepository) {
        this.activationTokenConsumer = activationTokenConsumer;
        this.userUpdateRepository = userUpdateRepository;
    }

    public void handle(ConfirmUserEmailChangeCommand command) {
        activationTokenConsumer.consumeWithData(command.token(), ActivationTokenType.EMAIL_CHANGE,
                data -> userUpdateRepository.updateEmail(data.userId(), data.newEmail()));
    }
}
