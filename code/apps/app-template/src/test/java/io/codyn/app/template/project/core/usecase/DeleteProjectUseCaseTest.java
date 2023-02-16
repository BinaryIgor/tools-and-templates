package io.codyn.app.template.project.core.usecase;

import io.codyn.app.template._common.core.exception.AccessForbiddenException;
import io.codyn.app.template.project.core.model.DeleteProjectCommand;
import io.codyn.app.template.project.test.FakeProjectRepository;
import io.codyn.app.template.project.test.TestProjectObjects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class DeleteProjectUseCaseTest {

    private DeleteProjectUseCase useCase;
    private FakeProjectRepository projectRepository;

    @BeforeEach
    void setup() {
        projectRepository = new FakeProjectRepository();
        useCase = new DeleteProjectUseCase(projectRepository);
    }

    @Test
    void shouldDeleteProject() {
        var project = TestProjectObjects.newProject();

        projectRepository.save(project);

        useCase.handle(new DeleteProjectCommand(project.id(), project.ownerId()));

        Assertions.assertThat(projectRepository.deletedProjectId).isEqualTo(project.id());
    }

    @Test
    void shouldNotAllowToDeleteNotUserProject() {
        var project = TestProjectObjects.newProject();

        projectRepository.save(project);

        var anotherUser = UUID.randomUUID();
        var command = new DeleteProjectCommand(project.id(), anotherUser);

        Assertions.assertThatThrownBy(() -> useCase.handle(command))
                .isEqualTo(projectForbiddenException(anotherUser, project.id()));
    }

    private AccessForbiddenException projectForbiddenException(UUID userId, UUID projectId) {
        return new AccessForbiddenException("%s user doesn't have access to %s project"
                .formatted(userId, projectId));
    }
}
