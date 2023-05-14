package io.codyn.app.template.project.core.usecase;

import io.codyn.app.template._common.core.exception.InvalidNameException;
import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template.project.core.ProjectExceptions;
import io.codyn.app.template.project.core.model.Project;
import io.codyn.app.template.project.test.FakeProjectRepository;
import io.codyn.app.template.project.test.TestProjectObjects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

public class SaveProjectUseCaseTest {

    private SaveProjectUseCase useCase;
    private FakeProjectRepository projectRepository;

    @BeforeEach
    void setup() {
        projectRepository = new FakeProjectRepository();
        useCase = new SaveProjectUseCase(projectRepository);
    }

    @ParameterizedTest
    @MethodSource("invalidSaveProjectCases")
    void shouldValidateProjectWhileSaving(Project project) {
        Assertions.assertThatThrownBy(() -> useCase.handle(project))
                .isEqualTo(new InvalidNameException(project.name()));
    }

    @Test
    void shouldCreateProject() {
        var newProject = TestProjectObjects.newProject();

        var returnedProject = useCase.handle(newProject);

        Assertions.assertThat(projectRepository.savedProject).isEqualTo(newProject);
        Assertions.assertThat(projectRepository.savedProject).isEqualTo(returnedProject);
    }

    @Test
    void shouldUpdateProject() {
        var newProject = TestProjectObjects.newProject();

        useCase.handle(newProject);

        var updatedProject = new Project(newProject.id(), newProject.ownerId(), "another name", 1);

        useCase.handle(updatedProject);

        Assertions.assertThat(projectRepository.savedProject).isEqualTo(updatedProject);
    }

    @Test
    void shouldThrowExceptionWhileUpdatingNotUserProject() {
        var newProject = TestProjectObjects.newProject();

        useCase.handle(newProject);

        var anotherOwner = UUID.randomUUID();
        var toSave = projectWithAnotherOwnerIdAndIncreasedVersion(newProject, anotherOwner);

        Assertions.assertThatThrownBy(() -> useCase.handle(toSave))
                .isEqualTo(ProjectExceptions.projectForbiddenException(anotherOwner, newProject.id()));

        Assertions.assertThat(projectRepository.savedProject).isEqualTo(newProject);
    }

    static Stream<Project> invalidSaveProjectCases() {
        var tooLogName = "a".repeat(FieldValidator.MAX_NAME_LENGTH + 1);

        return Stream.of(TestProjectObjects.newProject(""),
                TestProjectObjects.newProject(" "),
                TestProjectObjects.newProject(null),
                TestProjectObjects.newProject(tooLogName));
    }

    private Project projectWithAnotherOwnerIdAndIncreasedVersion(Project project, UUID ownerId) {
        return new Project(project.id(), ownerId, project.name(), project.version() + 1);
    }

}
