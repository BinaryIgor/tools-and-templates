package io.codyn.app.template.project.core.usecase;

import io.codyn.app.template._common.core.exception.AppException;
import io.codyn.app.template.project.core.ProjectAccessValidator;
import io.codyn.app.template.project.core.ProjectRepository;
import io.codyn.app.template.project.core.ProjectUsersRepository;
import io.codyn.app.template.project.core.model.RemoveUsersFromProjectCommand;
import org.springframework.stereotype.Component;

@Component
public class RemoveUsersFromProjectUseCase {

    static final int MAX_USERS_TO_REMOVE = 500;
    private final ProjectRepository projectRepository;
    private final ProjectUsersRepository projectUsersRepository;

    public RemoveUsersFromProjectUseCase(ProjectRepository projectRepository,
                                         ProjectUsersRepository projectUsersRepository) {
        this.projectRepository = projectRepository;
        this.projectUsersRepository = projectUsersRepository;
    }

    public void handle(RemoveUsersFromProjectCommand command) {
        if (command.toRemoveUserIds().size() > MAX_USERS_TO_REMOVE) {
            throw new TooManyUsersException();
        }

        ProjectAccessValidator.getOwnerAndValidateAccess(projectRepository,
                command.projectId(), command.userId());

        projectUsersRepository.removeUsers(command.projectId(), command.toRemoveUserIds());
    }

    public static class TooManyUsersException extends AppException {
        public TooManyUsersException() {
            super("Attempted to remove too many users. Max %d can be removed at the same time".formatted(
                    MAX_USERS_TO_REMOVE));
        }
    }
}
