package io.codyn.app.template.user.account.core.usecase;

import io.codyn.app.template.user.account.core.model.ConfirmUserEmailChangeCommand;
import io.codyn.app.template.user.common.core.PasswordHasher;
import io.codyn.app.template.user.common.core.repository.UserRepository;
import io.codyn.app.template.user.common.core.repository.UserUpdateRepository;
import org.springframework.stereotype.Component;

@Component
public class ConfirmUserEmailChangeUseCase {

    private final UserRepository userRepository;
    private final UserUpdateRepository userUpdateRepository;
    private final PasswordHasher passwordHasher;

    public ConfirmUserEmailChangeUseCase(UserRepository userRepository,
                                         UserUpdateRepository userUpdateRepository,
                                         PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.userUpdateRepository = userUpdateRepository;
        this.passwordHasher = passwordHasher;
    }

    public void handle(ConfirmUserEmailChangeCommand command) {
        //TODO: impl
    }
}
