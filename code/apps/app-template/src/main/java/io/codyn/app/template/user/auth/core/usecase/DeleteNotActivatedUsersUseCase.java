package io.codyn.app.template.user.auth.core.usecase;

import io.codyn.app.template.user.auth.core.repository.UserDeleteRepository;

import java.time.Clock;
import java.time.Duration;

public class DeleteNotActivatedUsersUseCase {

    private final UserDeleteRepository userDeleteRepository;
    private final Duration maxNotActivatedUser;
    private final Clock clock;

    public DeleteNotActivatedUsersUseCase(UserDeleteRepository userDeleteRepository,
                                          Duration maxNotActivatedUser,
                                          Clock clock) {
        this.userDeleteRepository = userDeleteRepository;
        this.maxNotActivatedUser = maxNotActivatedUser;
        this.clock = clock;
    }

    public void handle() {
        var before = clock.instant().minus(maxNotActivatedUser);
        userDeleteRepository.deleteAllNotActivatedCreatedBefore(before);
    }
}
