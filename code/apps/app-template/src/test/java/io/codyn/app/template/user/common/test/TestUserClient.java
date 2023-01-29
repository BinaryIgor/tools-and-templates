package io.codyn.app.template.user.common.test;

import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template.user.auth.core.model.User;
import io.codyn.app.template.user.auth.infra.SqlUserRepository;
import io.codyn.sqldb.core.DSLContextProvider;
import io.codyn.test.TestRandom;

import java.util.UUID;

public class TestUserClient {

    private final SqlUserRepository userRepository;

    public TestUserClient(DSLContextProvider contextProvider) {
        this.userRepository = new SqlUserRepository(contextProvider);
    }

    public UUID createUser(User user) {
        userRepository.create(user);
        return user.id();
    }

    //Add roles etc.
//    public void changeUserState(UUID id, UserState state) {
//
//    }

    public UUID createRandomUser(UUID id) {
        var name = TestRandom.string(5, 30);
        return createUser(User.newUser(id, name, name + "@email.com", "SomePass1234"));
    }
}
