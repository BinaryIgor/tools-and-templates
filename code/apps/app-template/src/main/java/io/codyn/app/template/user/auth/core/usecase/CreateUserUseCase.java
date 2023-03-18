package io.codyn.app.template.user.auth.core.usecase;

import io.codyn.app.template._common.core.email.Emails;
import io.codyn.app.template._common.core.exception.EmailNotReachableException;
import io.codyn.app.template._common.core.exception.EmailTakenException;
import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template.user.auth.core.model.CreateUserCommand;
import io.codyn.app.template.user.common.core.ActivationTokens;
import io.codyn.app.template.user.common.core.PasswordHasher;
import io.codyn.app.template.user.common.core.UserEmailSender;
import io.codyn.app.template.user.common.core.model.EmailUser;
import io.codyn.app.template.user.common.core.model.User;
import io.codyn.app.template.user.common.core.repository.UserRepository;
import io.codyn.types.Transactions;
import org.springframework.stereotype.Component;

@Component
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final ActivationTokens activationTokens;
    private final UserEmailSender emailSender;
    private final Transactions transactions;

    public CreateUserUseCase(UserRepository userRepository,
                             PasswordHasher passwordHasher,
                             ActivationTokens activationTokens,
                             UserEmailSender emailSender,
                             Transactions transactions) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.activationTokens = activationTokens;
        this.emailSender = emailSender;
        this.transactions = transactions;
    }

    //TODO: remove expired users!
    //TODO: allow to resend email!
    public void handle(CreateUserCommand command) {
        validateCommand(command);

        if (userRepository.ofEmail(command.email()).isPresent()) {
            throw new EmailTakenException(command.email());
        }

        var toCreateUser = User.newUser(command.id(), command.name(), command.email(),
                passwordHasher.hash(command.password()));

        var activationToken = transactions.executeAndReturn(() -> {
            userRepository.create(toCreateUser);
            return activationTokens.saveNewUser(toCreateUser.id());
        });

        sendAccountActivationEmail(toCreateUser.name(), toCreateUser.email(), activationToken.token());
    }

    private void validateCommand(CreateUserCommand command) {
        FieldValidator.validateName(command.name());

        var email = command.email();

        FieldValidator.validateEmail(email);

        if (!Emails.isReachable(email)) {
            throw new EmailNotReachableException(email);
        }

        FieldValidator.validatePassword(command.password());
    }

    private void sendAccountActivationEmail(String name, String email, String activationToken) {
        emailSender.sendAccountActivation(new EmailUser(name, email), activationToken);
    }
}
