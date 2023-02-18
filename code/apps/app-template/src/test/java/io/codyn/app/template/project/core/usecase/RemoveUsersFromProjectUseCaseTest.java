package io.codyn.app.template.project.core.usecase;

import io.codyn.app.template.project.core.ProjectExceptions;
import io.codyn.app.template.project.core.model.RemoveUsersFromProjectCommand;
import io.codyn.app.template.project.test.FakeProjectRepository;
import io.codyn.app.template.project.test.FakeProjectUsersRepository;
import io.codyn.app.template.project.test.TestProjectObjects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class RemoveUsersFromProjectUseCaseTest {

    private RemoveUsersFromProjectUseCase useCase;
    private FakeProjectRepository projectRepository;
    private FakeProjectUsersRepository projectUsersRepository;

    @BeforeEach
    void setup() {
        projectRepository = new FakeProjectRepository();
        projectUsersRepository = new FakeProjectUsersRepository();

        useCase = new RemoveUsersFromProjectUseCase(projectRepository, projectUsersRepository);
    }

    @Test
    void shouldThrowExceptionWhenAttemptingToRemoveTooManyUsers() {
        var tooManyUsersToRemove = Stream.generate(UUID::randomUUID)
                .limit(RemoveUsersFromProjectUseCase.MAX_USERS_TO_REMOVE + 1)
                .toList();

        var command = new RemoveUsersFromProjectCommand(UUID.randomUUID(), UUID.randomUUID(), tooManyUsersToRemove);

        Assertions.assertThatThrownBy(() -> useCase.handle(command))
                .isEqualTo(new RemoveUsersFromProjectUseCase.TooManyUsersException());
    }

    @Test
    void shouldNotAllowToRemoveUsersFromNotUserProject() {
        var project = TestProjectObjects.newProject();
        projectRepository.save(project);

        var projectId = project.id();
        var anotherUserId = UUID.randomUUID();

        var command = new RemoveUsersFromProjectCommand(projectId, anotherUserId, List.of(UUID.randomUUID()));

        Assertions.assertThatThrownBy(() -> useCase.handle(command))
                .isEqualTo(ProjectExceptions.projectForbiddenException(anotherUserId, projectId));
    }

    @Test
    void shouldRemoveUsersFromProject() {
        var project = TestProjectObjects.newProject();
        projectRepository.save(project);

        var projectUsers = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        projectUsersRepository.addUsers(project.id(), projectUsers);

        var projectId = project.id();
        var userId = project.ownerId();

        Assertions.assertThat(projectUsersRepository.usersOfProject(projectId))
                .isEqualTo(projectUsers);

        var usersToRemove = projectUsers.subList(0, 2);

        useCase.handle(new RemoveUsersFromProjectCommand(projectId, userId, usersToRemove));

        var remainingUsers = List.of(projectUsers.get(2));

        Assertions.assertThat(projectUsersRepository.usersOfProject(projectId))
                .isEqualTo(remainingUsers);

    }

}
