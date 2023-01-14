package io.codyn.app.template.user.domain.service;

import io.codyn.app.template._shared.domain.email.Emails;
import io.codyn.app.template._shared.domain.exception.EmailNotReachableException;
import io.codyn.app.template._shared.domain.exception.EmailTakenException;
import io.codyn.app.template._shared.domain.validator.FieldValidator;
import io.codyn.app.template.user.api.event.UserCreatedEvent;
import io.codyn.app.template.user.domain.component.PasswordHasher;
import io.codyn.app.template.user.domain.model.NewUserRequest;
import io.codyn.app.template.user.domain.model.User;
import io.codyn.app.template.user.domain.repository.UserRepository;
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
