package io.codyn.app.template.user.account.core;

import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template.user.account.core.model.UpdatePasswordRequest;
import io.codyn.app.template.user.auth.core.exception.NotMatchedPasswordException;
import io.codyn.app.template.user.common.core.PasswordHasher;
import io.codyn.app.template.user.common.core.UserExceptions;
import io.codyn.app.template.user.common.core.repository.UserRepository;
import io.codyn.app.template.user.common.core.repository.UserUpdateRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserAccountService {

    private final UserRepository userRepository;
    private final UserUpdateRepository userUpdateRepository;
    private final PasswordHasher passwordHasher;

    public UserAccountService(UserRepository userRepository,
                              UserUpdateRepository userUpdateRepository,
                              PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.userUpdateRepository = userUpdateRepository;
        this.passwordHasher = passwordHasher;
    }

    public void changeEmail(UUID id, String newEmail) {
        //TODO: impl
    }

    public void confirmEmailChange(UUID id, String token) {
        //TODO: impl
    }

    public void updatePassword(UUID id, UpdatePasswordRequest request) {
        FieldValidator.validatePassword(request.newPassword());

        var user = userRepository.ofId(id).orElseThrow(() -> UserExceptions.userOfIdNotFound(id));

        if (!passwordHasher.matches(request.oldPassword(), user.password())) {
            throw new NotMatchedPasswordException();
        }

        var hashedPassword = passwordHasher.hash(request.newPassword());
        userUpdateRepository.updatePassword(id, hashedPassword);
    }
}
