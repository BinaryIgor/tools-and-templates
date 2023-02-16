package io.codyn.app.template.project.core.usecase;

import io.codyn.app.template.project.core.ProjectAccessValidator;
import io.codyn.app.template.project.core.ProjectRepository;
import io.codyn.app.template.project.core.model.DeleteProjectCommand;
import org.springframework.stereotype.Component;

@Component
public class DeleteProjectUseCase {

    private final ProjectRepository projectRepository;

    public DeleteProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void handle(DeleteProjectCommand command) {
        ProjectAccessValidator.getOwnerAndValidateAccess(projectRepository,
                command.projectId(), command.userId());
        projectRepository.delete(command.projectId());
    }
}
