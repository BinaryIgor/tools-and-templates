package io.codyn.app.processor.template.user.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;

@Component
public class DeleteNotActivatedUsersUseCase {

    private final UserDeleteRepository userDeleteRepository;
    private final Duration maxNotActivatedUser;
    private final Clock clock;

    public DeleteNotActivatedUsersUseCase(UserDeleteRepository userDeleteRepository,
                                          @Value("${app.user.max-not-activated-user-duration}") Duration maxNotActivatedUser,
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
