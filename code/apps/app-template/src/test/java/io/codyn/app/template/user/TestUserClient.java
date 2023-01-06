package io.codyn.app.template.user;

import io.codyn.app.template.user.api.CurrentUser;
import io.codyn.app.template.user.api.UserClient;
import io.codyn.commons.test.TestRandom;
import org.jooq.DSLContext;

import java.util.UUID;

import static io.codyn.commons.sqldb.schema.user.Tables.USER;

public class TestUserClient implements UserClient {

    private final DSLContext context;
    private CurrentUser currentUser;

    public TestUserClient(DSLContext context) {
        this.context = context;
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

    public UUID createUser(NewUser user) {
        context.newRecord(USER)
                .setId(user.id)
                .setName(user.name)
                .setEmail(user.email)
                .setPassword(user.password)
                .insert();

        return user.id;
    }

    public UUID createRandomUser(UUID userId) {
        var name = TestRandom.string(5, 30);
        return createUser(new NewUser(userId, name, name + "@email.com"));
    }

    public record NewUser(UUID id,
                          String name,
                          String email,
                          String password) {

        public NewUser(UUID id, String name, String email) {
            this(id, name, email, "some-password");
        }
    }
}
