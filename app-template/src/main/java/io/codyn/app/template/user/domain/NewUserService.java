package io.codyn.app.template.user.domain;

import io.codyn.app.template._shared.domain.ResourceExistsException;
import io.codyn.app.template.user.domain.model.NewUser;
import io.codyn.app.template.user.domain.repository.NewUserRepository;
import io.codyn.app.template.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewUserService {

    private final NewUserRepository newUserRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public NewUserService(NewUserRepository newUserRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.newUserRepository = newUserRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void create(NewUser user) {
        validateUser(user);

        //TODO: improve
        if (userRepository.findByEmail(user.email()).isPresent()) {
            throw new ResourceExistsException("User of %s email exists".formatted(user.email()),
                    "EMAIL_TAKEN");
        }

        var userId = newUserRepository.create(user);
        //TODO send email!
    }

    private void validateUser(NewUser user) {
        //TODO
    }
}
