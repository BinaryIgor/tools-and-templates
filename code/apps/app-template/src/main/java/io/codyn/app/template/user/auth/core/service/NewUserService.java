package io.codyn.app.template.user.auth.core.service;

import io.codyn.app.template._common.core.email.Emails;
import io.codyn.app.template._common.core.exception.EmailNotReachableException;
import io.codyn.app.template._common.core.exception.EmailTakenException;
import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template.user.api.event.UserCreatedEvent;
import io.codyn.app.template.user.auth.core.model.NewUserRequest;
import io.codyn.app.template.user.auth.core.model.User;
import io.codyn.app.template.user.auth.core.repository.UserRepository;
import io.codyn.app.template.user.common.core.PasswordHasher;
import io.codyn.types.EventHandler;
import io.codyn.types.Transactions;
import org.springframework.stereotype.Service;

@Service
public class NewUserService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final Transactions transactions;
    private final EventHandler<UserCreatedEvent> userCreatedEventHandler;

    public NewUserService(UserRepository userRepository,
                          PasswordHasher passwordHasher,
                          Transactions transactions,
                          EventHandler<UserCreatedEvent> userCreatedEventHandler) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.transactions = transactions;
        this.userCreatedEventHandler = userCreatedEventHandler;
    }

    public void create(NewUserRequest user) {
        validateUser(user);

        if (userRepository.ofEmail(user.email()).isPresent()) {
            throw new EmailTakenException(user.email());
        }

        var toCreateUser = User.newUser(user.id(), user.name(), user.email(), passwordHasher.hash(user.password()));

        transactions.execute(() -> {
            userRepository.create(toCreateUser);
            userCreatedEventHandler.handle(new UserCreatedEvent(user.id(), user.name(), user.email()));
        });
    }

    private void validateUser(NewUserRequest user) {
        FieldValidator.validateName(user.name());

        var email = user.email();

        FieldValidator.validateEmail(email);

        if (!Emails.isReachable(email)) {
            throw new EmailNotReachableException(email);
        }

        FieldValidator.validatePassword(user.password());
    }
}
