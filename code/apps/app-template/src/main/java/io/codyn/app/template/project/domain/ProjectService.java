package io.codyn.app.template.project.domain;

import io.codyn.app.template._shared.domain.exception.AccessForbiddenException;
import io.codyn.app.template._shared.domain.exception.NotFoundException;
import io.codyn.app.template._shared.domain.validator.FieldValidator;
import io.codyn.app.template.project.domain.model.AddUsersToProjectCommand;
import io.codyn.app.template.project.domain.model.Project;
import io.codyn.app.template.project.domain.model.ProjectWithUsers;
import io.codyn.app.template.project.domain.model.RemoveUsersFromProjectCommand;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectUsersRepository projectUsersRepository;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectUsersRepository projectUsersRepository) {
        this.projectRepository = projectRepository;
        this.projectUsersRepository = projectUsersRepository;
    }

    public void save(Project project) {
        validateProject(project);

        if (project.version() == 0) {
            projectRepository.save(project);
        } else {
            getOwnerAndValidateAccessToProject(project.id(), project.ownerId());
            projectRepository.save(project);
        }
    }

    private void validateProject(Project project) {
        FieldValidator.validateName(project.name());
    }

    private void getOwnerAndValidateAccessToProject(UUID projectId, UUID userId) {
        var currentProjectOwnerId = projectRepository.findOwnerById(projectId)
                .orElseThrow(() -> new NotFoundException(
                        "There is no project of %s id".formatted(projectId)));

        validateAccessToProject(projectId, userId, currentProjectOwnerId);
    }

    private void validateAccessToProject(UUID projectId, UUID userId, UUID currentProjectOwnerId) {
        if (!userId.equals(currentProjectOwnerId)) {
            throw new AccessForbiddenException("%s user doesn't have access to %s project"
                    .formatted(userId, projectId));
        }
    }

    public void delete(UUID projectId, UUID userId) {
        getOwnerAndValidateAccessToProject(projectId, userId);
        projectRepository.delete(projectId);
    }

    public ProjectWithUsers get(UUID projectId, UUID userId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(
                        "There is no project of %s id".formatted(projectId)));

        validateAccessToProject(projectId, userId, project.ownerId());

        var projectUsers = projectUsersRepository.usersOfProject(projectId);

        return new ProjectWithUsers(project, projectUsers);
    }

    public void addUsers(AddUsersToProjectCommand command) {
        getOwnerAndValidateAccessToProject(command.projectId(), command.userId());
        projectUsersRepository.addUsers(command.projectId(), command.toAddUserIds());
    }

    public void removeUsers(RemoveUsersFromProjectCommand command) {
        getOwnerAndValidateAccessToProject(command.projectId(), command.userId());
        projectUsersRepository.removeUsers(command.projectId(), command.toDeleteUserIds());
    }
}
