package io.codyn.app.template.user.auth;

import io.codyn.app.template.user.auth.core.usecase.DeleteNotActivatedUsersUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserAuthModuleScheduler {

    private final DeleteNotActivatedUsersUseCase deleteNotActivatedUsersUseCase;

    public UserAuthModuleScheduler(DeleteNotActivatedUsersUseCase deleteNotActivatedUsersUseCase) {
        this.deleteNotActivatedUsersUseCase = deleteNotActivatedUsersUseCase;
    }

    @Scheduled(fixedDelayString = "${app.user.delete-not-activated-delay}")
    public void deleteNotActivatedUsers() {
        deleteNotActivatedUsersUseCase.handle();
    }
}
