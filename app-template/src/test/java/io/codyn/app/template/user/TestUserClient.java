package io.codyn.app.template.user;

import io.codyn.app.template.user.api.CurrentUser;
import io.codyn.app.template.user.api.UserClient;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

public class TestUserClient implements UserClient {

    private final CurrentUser currentUser;

    public TestUserClient(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }


    @Override
    public CurrentUser currentUser() {
        return currentUser;
    }

    @Override
    public UUID currentUserId() {
        return currentUser.id();
    }

    public static UUID createUser(JdbcTemplate template, NewUser user) {
        template.update("""
                INSERT INTO "user".user (id, name, email, password)
                VALUES (?, ?, ?, ?)
                """, user.id, user.name, user.email, user.password);

        return user.id;
    }

    public static UUID createUser(JdbcTemplate template, UUID userId) {
        return createUser(template, new NewUser(userId, "some-name", "some-email@email.com"));
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
