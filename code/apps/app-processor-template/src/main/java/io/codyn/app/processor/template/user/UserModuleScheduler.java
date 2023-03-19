package io.codyn.app.processor.template.user;

import io.codyn.app.processor.template.user.core.DeleteNotActivatedUsersUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserModuleScheduler {

    private final DeleteNotActivatedUsersUseCase deleteNotActivatedUsersUseCase;

    public UserModuleScheduler(DeleteNotActivatedUsersUseCase deleteNotActivatedUsersUseCase) {
        this.deleteNotActivatedUsersUseCase = deleteNotActivatedUsersUseCase;
    }

    @Scheduled(fixedDelayString = "${app.user.delete-not-activated-delay}")
    public void deleteNotActivatedUsers() {
        //TODO: logs/metrics
        deleteNotActivatedUsersUseCase.handle();
    }
}
