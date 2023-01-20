package io.codyn.app.template.user;

import io.codyn.app.template.user.api.CurrentUser;
import io.codyn.app.template.user.api.UserClient;
import io.codyn.app.template.user.core.model.User;
import io.codyn.app.template.user.infra.SqlUserRepository;
import io.codyn.sqldb.core.DSLContextProvider;
import io.codyn.test.TestRandom;

import java.util.UUID;

public class TestUserClient implements UserClient {

    private final SqlUserRepository userRepository;
    private CurrentUser currentUser;

    public TestUserClient(DSLContextProvider contextProvider) {
        this.userRepository = new SqlUserRepository(contextProvider);
    }

    public void setCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    public void setCurrentUser(UUID id) {
        setCurrentUser(new CurrentUser(id));
    }

    public UUID setRandomCurrentUser() {
        var id = UUID.randomUUID();
        setCurrentUser(id);
        return id;
    }

    @Override
    public CurrentUser currentUser() {
        if (currentUser == null) {
            throw new RuntimeException("Current user not set!");
        }
        return currentUser;
    }

    @Override
    public UUID currentUserId() {
        return currentUser().id();
    }

    public UUID createUser(User user) {
        userRepository.create(user);
        return user.id();
    }

    public UUID createRandomUser(UUID userId) {
        var name = TestRandom.string(5, 30);
        return createUser(User.newUser(userId, name, name + "@email.com", "SomePass1234"));
    }
}
