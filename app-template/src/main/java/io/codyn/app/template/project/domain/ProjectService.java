package io.codyn.app.template.project.domain;

import io.codyn.app.template._shared.domain.exception.AppResourceForbiddenException;
import io.codyn.app.template._shared.domain.exception.AppResourceNotFoundException;
import io.codyn.app.template._shared.domain.validator.FieldValidator;
import io.codyn.app.template.project.domain.model.AddUsersToProjectCommand;
import io.codyn.app.template.project.domain.model.RemoveUsersFromProjectCommand;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void save(Project project) {
        validateProject(project);

        if (project.version() == 0) {
            projectRepository.save(project);
        } else {
            validateAccessToProject(project.id(), project.ownerId());
            projectRepository.save(project);
        }
    }

    private void validateProject(Project project) {
        FieldValidator.validateName(project.name());
    }

    private void validateAccessToProject(UUID projectId, UUID userId) {
        var currentProjectOwnerId = projectRepository.findOwnerById(projectId)
                .orElseThrow(() -> new AppResourceNotFoundException(
                        "There is no project of %s id".formatted(projectId)));

        if (!userId.equals(currentProjectOwnerId)) {
            throw new AppResourceForbiddenException("%s user doesn't have access to %s project"
                    .formatted(userId, projectId));
        }
    }

    public void delete(UUID projectId, UUID userId) {
        validateAccessToProject(projectId, userId);
        projectRepository.delete(projectId);
    }

    public void addUsers(AddUsersToProjectCommand command) {
        validateAccessToProject(command.projectId(), command.userId());
        projectRepository.addUsers(command.userId(), command.toAddUserIds());
    }

    public void removeUsers(RemoveUsersFromProjectCommand command) {
        validateAccessToProject(command.projectId(), command.userId());
        projectRepository.removeUsers(command.projectId(), command.toDeleteUserIds());
    }
}
