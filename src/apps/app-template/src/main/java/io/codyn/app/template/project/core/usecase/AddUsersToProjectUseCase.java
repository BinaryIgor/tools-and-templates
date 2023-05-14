package io.codyn.app.template.project.core.usecase;

import io.codyn.app.template._common.core.exception.AppException;
import io.codyn.app.template.project.core.ProjectAccessValidator;
import io.codyn.app.template.project.core.ProjectRepository;
import io.codyn.app.template.project.core.ProjectUsersRepository;
import io.codyn.app.template.project.core.model.AddUsersToProjectCommand;
import org.springframework.stereotype.Component;

@Component
public class AddUsersToProjectUseCase {

    static final int MAX_USERS_TO_ADD = 100;
    private final ProjectRepository projectRepository;
    private final ProjectUsersRepository projectUsersRepository;

    public AddUsersToProjectUseCase(ProjectRepository projectRepository,
                                    ProjectUsersRepository projectUsersRepository) {
        this.projectRepository = projectRepository;
        this.projectUsersRepository = projectUsersRepository;
    }

    public void handle(AddUsersToProjectCommand command) {
        if (command.toAddUserIds().size() > MAX_USERS_TO_ADD) {
            throw new TooManyUsersException();
        }

        ProjectAccessValidator.getOwnerAndValidateAccess(projectRepository,
                command.projectId(), command.userId());

        projectUsersRepository.addUsers(command.projectId(), command.toAddUserIds());
    }

    public static class TooManyUsersException extends AppException {
        public TooManyUsersException() {
            super("Attempted to add too many users. Max %d can be added at the same time".formatted(MAX_USERS_TO_ADD));
        }
    }
}
