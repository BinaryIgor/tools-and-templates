package io.codyn.app.template.user.domain;

import io.codyn.app.template._shared.domain.validator.FieldValidator;
import io.codyn.app.template.user.api.event.UserCreatedEvent;
import io.codyn.app.template.user.domain.model.NewUser;
import io.codyn.app.template.user.domain.repository.NewUserRepository;
import io.codyn.app.template.user.domain.repository.UserRepository;
import io.codyn.commons.types.EventPublisher;
import io.codyn.commons.types.Transactions;
import org.springframework.stereotype.Service;

@Service
public class NewUserService {

    private final NewUserRepository newUserRepository;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final Transactions transactions;
    private final EventPublisher eventPublisher;

    public NewUserService(NewUserRepository newUserRepository,
                          UserRepository userRepository,
                          PasswordHasher passwordHasher,
                          Transactions transactions,
                          EventPublisher eventPublisher) {
        this.newUserRepository = newUserRepository;
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.transactions = transactions;
        this.eventPublisher = eventPublisher;
    }

    public void create(NewUser user) {
        validateUser(user);

        if (userRepository.findByEmail(user.email()).isPresent()) {
            throw UserExceptions.emailTaken();
        }

        var hashedUser = user.withPassword(passwordHasher.hash(user.password()));

        transactions.execute(() -> {
            var userId = newUserRepository.create(hashedUser);
            //Or maybe just user created handler?
            eventPublisher.publish(new UserCreatedEvent(userId, user.name(), user.email()));
        });
    }

    private void validateUser(NewUser user) {
        FieldValidator.validateName(user.name());
        FieldValidator.validateEmail(user.email());
        FieldValidator.validatePassword(user.password());
    }
}
