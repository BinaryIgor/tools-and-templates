package io.codyn.app.template.user.auth.core.usecase;

import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template.auth.api.AuthClient;
import io.codyn.app.template.user.auth.core.exception.NotMatchedPasswordException;
import io.codyn.app.template.user.auth.core.model.CurrentUserData;
import io.codyn.app.template.user.auth.core.model.SignInFirstStepCommand;
import io.codyn.app.template.user.auth.core.model.SignedInUser;
import io.codyn.app.template.user.auth.core.model.SignedInUserStep;
import io.codyn.app.template.user.common.core.PasswordHasher;
import io.codyn.app.template.user.common.core.UserExceptions;
import io.codyn.app.template.user.common.core.model.User;
import io.codyn.app.template.user.common.core.repository.UserAuthRepository;
import io.codyn.app.template.user.common.core.repository.UserRepository;

public class SignInFirstStepUseCase {

    private final AuthClient authClient;
    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;
    private final PasswordHasher passwordHasher;

    public SignInFirstStepUseCase(AuthClient authClient,
                                  UserRepository userRepository,
                                  UserAuthRepository userAuthRepository,
                                  PasswordHasher passwordHasher) {
        this.authClient = authClient;
        this.userRepository = userRepository;
        this.userAuthRepository = userAuthRepository;
        this.passwordHasher = passwordHasher;
    }

    public SignedInUserStep handle(SignInFirstStepCommand command) {
        validateCommand(command);

        var user = validatedUser(command.email(), command.password());
        if (user.secondFactorAuth()) {
            //TODO: impl!
            throw new RuntimeException("Two factor authentication not supported!");
        }

        return SignedInUserStep.onlyStep(toSignedInUser(user));
    }

    private void validateCommand(SignInFirstStepCommand command) {
        FieldValidator.validateEmail(command.email());
        FieldValidator.validatePassword(command.password());
    }

    private User validatedUser(String email, String password) {
        var user = userRepository.ofEmail(email)
                .orElseThrow(() -> UserExceptions.userOfEmailNotFound(email));

        if (passwordHasher.matches(password, user.password())) {
            return user;
        }

        throw new NotMatchedPasswordException();
    }

    private SignedInUser toSignedInUser(User user) {
        var userRoles = userAuthRepository.rolesOfUser(user.id());

        var currentUser = new CurrentUserData(user.id(), user.email(), user.name(),
                user.state(), userRoles);

        return new SignedInUser(currentUser, authClient.ofUser(user.id()));
    }
}
