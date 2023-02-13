package io.codyn.app.template.user.account.core.usecase;

import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template.user.account.core.model.UpdateUserPasswordCommand;
import io.codyn.app.template.user.auth.core.exception.NotMatchedPasswordException;
import io.codyn.app.template.user.common.core.PasswordHasher;
import io.codyn.app.template.user.common.core.UserExceptions;
import io.codyn.app.template.user.common.core.repository.UserRepository;
import io.codyn.app.template.user.common.core.repository.UserUpdateRepository;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserPasswordUseCase {

    private final UserRepository userRepository;
    private final UserUpdateRepository userUpdateRepository;
    private final PasswordHasher passwordHasher;

    public UpdateUserPasswordUseCase(UserRepository userRepository,
                                     UserUpdateRepository userUpdateRepository,
                                     PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.userUpdateRepository = userUpdateRepository;
        this.passwordHasher = passwordHasher;
    }

    public void handle(UpdateUserPasswordCommand command) {
        FieldValidator.validatePassword(command.newPassword());

        var user = userRepository.ofId(command.id())
                .orElseThrow(() -> UserExceptions.userOfIdNotFound(command.id()));

        if (!passwordHasher.matches(command.oldPassword(), user.password())) {
            throw new NotMatchedPasswordException();
        }

        var hashedPassword = passwordHasher.hash(command.newPassword());
        userUpdateRepository.updatePassword(command.id(), hashedPassword);
    }
}
