package io.codyn.app.template.project.core.usecase;

import io.codyn.app.template.project.core.ProjectExceptions;
import io.codyn.app.template.project.core.model.GetProjectWithUsersQuery;
import io.codyn.app.template.project.core.model.ProjectWithUsers;
import io.codyn.app.template.project.test.FakeProjectRepository;
import io.codyn.app.template.project.test.FakeProjectUsersRepository;
import io.codyn.app.template.project.test.TestProjectObjects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

public class GetProjectWithUsersUseCaseTest {

    private GetProjectWithUsersUseCase useCase;
    private FakeProjectRepository projectRepository;
    private FakeProjectUsersRepository projectUsersRepository;

    @BeforeEach
    void setup() {
        projectRepository = new FakeProjectRepository();
        projectUsersRepository = new FakeProjectUsersRepository();

        useCase = new GetProjectWithUsersUseCase(projectRepository, projectUsersRepository);
    }

    @Test
    void shouldReturnProjectWithUsers() {
        var projectWithUsers = prepareProjectWithUsers();
        var project = projectWithUsers.project();
        var query = new GetProjectWithUsersQuery(project.id(), project.ownerId());

        Assertions.assertThat(useCase.handle(query))
                .isEqualTo(projectWithUsers);
    }

    @Test
    void shouldNotAllowToGetNotUserProjectWithUsers() {
        var projectWithUsers = prepareProjectWithUsers();
        var projectId = projectWithUsers.project().id();
        var anotherUserId = UUID.randomUUID();
        var query = new GetProjectWithUsersQuery(projectId, anotherUserId);

        Assertions.assertThatThrownBy(() -> useCase.handle(query))
                .isEqualTo(ProjectExceptions.projectForbiddenException(anotherUserId, projectId));
    }

    private ProjectWithUsers prepareProjectWithUsers() {
        var project = TestProjectObjects.newProject();
        projectRepository.save(project);

        var projectUsers = List.of(UUID.randomUUID(), UUID.randomUUID());
        projectUsersRepository.addUsers(project.id(), projectUsers);

        return new ProjectWithUsers(project, projectUsers);
    }
}
