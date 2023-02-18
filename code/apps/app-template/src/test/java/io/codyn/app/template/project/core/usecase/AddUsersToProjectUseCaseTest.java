package io.codyn.app.template.project.core.usecase;

import io.codyn.app.template.project.core.ProjectExceptions;
import io.codyn.app.template.project.core.model.AddUsersToProjectCommand;
import io.codyn.app.template.project.test.FakeProjectRepository;
import io.codyn.app.template.project.test.FakeProjectUsersRepository;
import io.codyn.app.template.project.test.TestProjectObjects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class AddUsersToProjectUseCaseTest {

    private AddUsersToProjectUseCase useCase;
    private FakeProjectRepository projectRepository;
    private FakeProjectUsersRepository projectUsersRepository;

    @BeforeEach
    void setup() {
        projectRepository = new FakeProjectRepository();
        projectUsersRepository = new FakeProjectUsersRepository();

        useCase = new AddUsersToProjectUseCase(projectRepository, projectUsersRepository);
    }

    @Test
    void shouldThrowExceptionWhenAttemptingToAddTooManyUsers() {
        var tooManyUsersToAdd = Stream.generate(UUID::randomUUID)
                .limit(AddUsersToProjectUseCase.MAX_USERS_TO_ADD + 1)
                .toList();

        var command = new AddUsersToProjectCommand(UUID.randomUUID(), UUID.randomUUID(), tooManyUsersToAdd);

        Assertions.assertThatThrownBy(() -> useCase.handle(command))
                .isEqualTo(new AddUsersToProjectUseCase.TooManyUsersException());
    }

    @Test
    void shouldNotAllowToAddUsersToNotUserProject() {
        var project = TestProjectObjects.newProject();
        projectRepository.save(project);

        var projectId = project.id();
        var anotherUserId = UUID.randomUUID();

        var command = new AddUsersToProjectCommand(projectId, anotherUserId, List.of(UUID.randomUUID()));

        Assertions.assertThatThrownBy(() -> useCase.handle(command))
                .isEqualTo(ProjectExceptions.projectForbiddenException(anotherUserId, projectId));
    }

    @Test
    void shouldAddUsersToProject() {
        var project = TestProjectObjects.newProject();
        projectRepository.save(project);

        var projectId = project.id();
        var userId = project.ownerId();

        Assertions.assertThat(projectUsersRepository.usersOfProject(projectId)).isEmpty();

        var firstUsers = List.of(UUID.randomUUID(), UUID.randomUUID());

        useCase.handle(new AddUsersToProjectCommand(projectId, userId, firstUsers));

        Assertions.assertThat(projectUsersRepository.usersOfProject(projectId))
                .isEqualTo(firstUsers);

        var nextUsers = List.of(UUID.randomUUID());

        useCase.handle(new AddUsersToProjectCommand(projectId, userId, nextUsers));

        var allProjectUsers = new ArrayList<>(firstUsers);
        allProjectUsers.addAll(nextUsers);

        Assertions.assertThat(projectUsersRepository.usersOfProject(projectId))
                .isEqualTo(allProjectUsers);
    }

}
