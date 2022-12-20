package io.codyn.app.template.user.infra;

import io.codyn.app.template.user.domain.model.User;
import io.codyn.app.template.user.domain.model.UserState;
import io.codyn.app.template.user.domain.repository.UserRepository;
import io.codyn.app.template.user.infra.entity.UserEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SqlUserRepository implements UserRepository {

    private final UserEntityRepository userEntityRepository;

    public SqlUserRepository(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userEntityRepository.findByEmail(email)
                .map(e -> new User(e.id(), e.name(), e.email(), e.password(), UserState.valueOf(e.state())));
    }
}
