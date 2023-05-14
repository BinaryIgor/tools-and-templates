package io.codyn.app.template.project.core.usecase;

import io.codyn.app.template.project.core.ProjectAccessValidator;
import io.codyn.app.template.project.core.ProjectExceptions;
import io.codyn.app.template.project.core.ProjectRepository;
import io.codyn.app.template.project.core.ProjectUsersRepository;
import io.codyn.app.template.project.core.model.GetProjectWithUsersQuery;
import io.codyn.app.template.project.core.model.ProjectWithUsers;
import org.springframework.stereotype.Component;

@Component
public class GetProjectWithUsersUseCase {

    private final ProjectRepository projectRepository;
    private final ProjectUsersRepository projectUsersRepository;

    public GetProjectWithUsersUseCase(ProjectRepository projectRepository,
                                      ProjectUsersRepository projectUsersRepository) {
        this.projectRepository = projectRepository;
        this.projectUsersRepository = projectUsersRepository;
    }

    public ProjectWithUsers handle(GetProjectWithUsersQuery query) {
        var projectId = query.projectId();
        var userId = query.userId();

        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFoundException(projectId));

        ProjectAccessValidator.validateAccess(projectId, userId, project.ownerId());

        var projectUsers = projectUsersRepository.usersOfProject(projectId);

        return new ProjectWithUsers(project, projectUsers);
    }
}
