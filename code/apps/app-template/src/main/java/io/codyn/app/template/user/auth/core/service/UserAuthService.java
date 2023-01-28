package io.codyn.app.template.user.auth.core.service;

import io.codyn.app.template._common.core.exception.NotFoundException;
import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template.auth.api.AuthClient;
import io.codyn.app.template.auth.core.AuthTokens;
import io.codyn.app.template.user.auth.core.exception.InvalidPasswordException;
import io.codyn.app.template.user.auth.core.model.*;
import io.codyn.app.template.user.auth.core.repository.UserAuthRepository;
import io.codyn.app.template.user.auth.core.repository.UserRepository;
import io.codyn.app.template.user.common.core.PasswordHasher;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService {

    private final AuthClient authClient;
    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;
    private final PasswordHasher passwordHasher;

    public UserAuthService(AuthClient authClient,
                           UserRepository userRepository,
                           UserAuthRepository userAuthRepository,
                           PasswordHasher passwordHasher) {
        this.authClient = authClient;
        this.userRepository = userRepository;
        this.userAuthRepository = userAuthRepository;
        this.passwordHasher = passwordHasher;
    }

    public SignedInUserStep authenticate(UserSignInRequest request) {
        validateSignInRequest(request);

        var user = validatedUser(request.email(), request.password());
        if (user.secondFactorAuth()) {
            throw new RuntimeException("Two factor authentication not supported!");
        }

        return SignedInUserStep.onlyStep(toSignedInUser(user));
    }

    private void validateSignInRequest(UserSignInRequest request) {
        FieldValidator.validateEmail(request.email());
        FieldValidator.validatePassword(request.password());
    }

    private User validatedUser(String email, String password) {
        var user = userRepository.ofEmail(email)
                .orElseThrow(() -> new NotFoundException(
                        "User of %s email doesn't exist".formatted(email)));

        if (passwordHasher.matches(password, user.password())) {
            return user;
        }

        throw new InvalidPasswordException();
    }

    private SignedInUser toSignedInUser(User user) {
        var userRoles = userAuthRepository.rolesOfUser(user.id());

        var currentUser = new CurrentUserData(user.id(), user.email(), user.name(),
                user.state(), userRoles);

        return new SignedInUser(currentUser, authClient.ofUser(user.id()));
    }

    //TODO: impl
    public SignedInUser authenticateSecondStep(UserSignInSecondStepRequest request) {
        return null;
    }

    public AuthTokens newTokens(String refreshToken) {
        return authClient.refresh(refreshToken);
    }
}
