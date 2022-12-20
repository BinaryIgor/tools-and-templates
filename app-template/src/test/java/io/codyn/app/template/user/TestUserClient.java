package io.codyn.app.template.user;

import io.codyn.app.template.test.Tests;
import io.codyn.app.template.user.api.CurrentUser;
import io.codyn.app.template.user.api.UserClient;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

public class TestUserClient implements UserClient {

    private final JdbcTemplate jdbcTemplate;
    private CurrentUser currentUser;

    public TestUserClient(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
        jdbcTemplate.update("""
                INSERT INTO "user".user (id, name, email, password)
                VALUES (?, ?, ?, ?)
                """, user.id, user.name, user.email, user.password);

        return user.id;
    }

    public UUID createRandomUser(UUID userId) {
        var name = Tests.randomString(5, 30);
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
