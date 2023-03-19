package io.codyn.app.processor.template.user;

import io.codyn.app.processor.template.SpringIntegrationTest;
import io.codyn.app.processor.template.user.test.TestSqlUserClient;
import io.codyn.app.processor.template.user.core.UserState;
import io.codyn.sqldb.core.DSLContextProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class UserModuleSchedulerTest extends SpringIntegrationTest {

    @Autowired
    private UserModuleScheduler scheduler;
    @Autowired
    private DSLContextProvider contextProvider;
    private TestSqlUserClient userClient;

    @BeforeEach
    void setup() {
        userClient = new TestSqlUserClient(contextProvider);
    }

    @Test
    void shouldDeleteNotActivatedUsers() {
        var testCase = prepareDeleteNotActivatedUsersTestCase();

        testCase.beforeDeleteUsers.forEach(uid ->
                Assertions.assertThat(userClient.userOfId(uid))
                        .isPresent());

        scheduler.deleteNotActivatedUsers();

        testCase.expectedDeletedUsers.forEach(uid ->
                Assertions.assertThat(userClient.userOfId(uid))
                        .isEmpty());
        testCase.expectedExistingUsers.forEach(uid ->
                Assertions.assertThat(userClient.userOfId(uid))
                        .isPresent());
    }

    private DeleteNotActivatedUsersTestCase prepareDeleteNotActivatedUsersTestCase() {
        var now = Instant.now();

        var newCreatedUser1 = prepareUser(UserState.CREATED, now);
        var newCreatedUser2 = prepareUser(UserState.CREATED, now.minus(Duration.ofMinutes(5)));
        var newCreatedUser3 = prepareUser(UserState.CREATED, now.minus(Duration.ofMinutes(14)));

        var oldCreatedUser1 = prepareUser(UserState.CREATED, now.minus(Duration.ofMinutes(15)));
        var oldCreatedUser2 = prepareUser(UserState.CREATED, now.minus(Duration.ofDays(1)));

        var newActivatedUser = prepareUser(UserState.ACTIVATED, now);

        var oldActivatedUser = prepareUser(UserState.ACTIVATED, now.minus(Duration.ofMinutes(20)));
        var oldOnboardedUser = prepareUser(UserState.ONBOARDED, now.minus(Duration.ofDays(1)));
        var oldBannedUser = prepareUser(UserState.ONBOARDED, now.minus(Duration.ofDays(12)));

        var beforeDeleteUsers = List.of(newCreatedUser1, newCreatedUser2, newCreatedUser3,
                oldCreatedUser1, oldCreatedUser2, newActivatedUser,
                oldActivatedUser, oldOnboardedUser, oldBannedUser);

        var expectedDeletedUsers = List.of(oldCreatedUser1, oldCreatedUser2);

        var expectedExistingUsers = beforeDeleteUsers.stream()
                .filter(uid -> !expectedDeletedUsers.contains(uid))
                .toList();

        return new DeleteNotActivatedUsersTestCase(beforeDeleteUsers, expectedDeletedUsers, expectedExistingUsers);
    }

    private UUID prepareUser(UserState state, Instant createdAt) {
        var user = userClient.createRandomUser(UUID.randomUUID(), state);
        userClient.updateUserCreatedAt(user.id(), createdAt);
        return user.id();
    }

    record DeleteNotActivatedUsersTestCase(List<UUID> beforeDeleteUsers,
                                           List<UUID> expectedDeletedUsers,
                                           List<UUID> expectedExistingUsers) {
    }
}
