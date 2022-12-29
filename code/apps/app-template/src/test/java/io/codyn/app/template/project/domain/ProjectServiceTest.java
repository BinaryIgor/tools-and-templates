package io.codyn.app.template.project.domain;

import io.codyn.app.template._shared.domain.exception.AppResourceForbiddenException;
import io.codyn.app.template._shared.domain.exception.AppValidationException;
import io.codyn.app.template._shared.domain.validator.FieldValidator;
import io.codyn.app.template.project.domain.model.Project;
import io.codyn.app.template.project.domain.model.ProjectWithUsers;
import io.codyn.app.template.project.test.FakeProjectRepository;
import io.codyn.app.template.project.test.FakeProjectUsersRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

//TODO: more cases
public class ProjectServiceTest {

    private ProjectService service;
    private FakeProjectRepository projectRepository;
    private FakeProjectUsersRepository projectUsersRepository;

    @BeforeEach
    void setup() {
        projectRepository = new FakeProjectRepository();
        projectUsersRepository = new FakeProjectUsersRepository();

        service = new ProjectService(projectRepository, projectUsersRepository);
    }

    @ParameterizedTest
    @MethodSource("invalidProjectCases")
    void shouldValidateProjectWhileSaving(Project project) {
        Assertions.assertThatThrownBy(() -> service.save(project))
                .isEqualTo(AppValidationException.ofField("name", project.name()));
    }

    @Test
    void shouldCreateProject() {
        var newProject = newProject("some-name");

        service.save(newProject);

        Assertions.assertThat(projectRepository.savedProject).isEqualTo(newProject);
    }

    @Test
    void shouldUpdateProject() {
        var newProject = newProject("some-project");

        service.save(newProject);

        var updatedProject = new Project(newProject.id(), newProject.ownerId(), "another name", 1);

        service.save(updatedProject);

        Assertions.assertThat(projectRepository.savedProject).isEqualTo(updatedProject);
    }

    @Test
    void shouldThrowExceptionWhileUpdatingNotUserProject() {
        var newProject = newProject("some-project 2");

        service.save(newProject);

        var anotherOwner = UUID.randomUUID();
        var toSave = projectWithAnotherOwnerIdAndIncreasedVersion(newProject, anotherOwner);

        Assertions.assertThatThrownBy(() -> service.save(toSave))
                .isEqualTo(projectForbiddenException(anotherOwner, newProject.id()));

        Assertions.assertThat(projectRepository.savedProject).isEqualTo(newProject);
    }

    @Test
    void shouldDeleteProject() {
        var project = newProject("some project");

        projectRepository.save(project);

        service.delete(project.id(), project.ownerId());

        Assertions.assertThat(projectRepository.deletedProjectId).isEqualTo(project.id());
    }

    @Test
    void shouldNotAllowToDeleteNotUserProject() {
        var project = newProject("some project");

        projectRepository.save(project);

        var anotherUser = UUID.randomUUID();

        Assertions.assertThatThrownBy(() -> service.delete(project.id(), anotherUser))
                .isEqualTo(projectForbiddenException(anotherUser, project.id()));
    }

    @Test
    void shouldReturnProjectWithUsers() {
        var projectWithUsers = prepareProjectWithUsers();
        var project = projectWithUsers.project();

        Assertions.assertThat(service.get(project.id(), project.ownerId()))
                .isEqualTo(projectWithUsers);
    }

    @Test
    void shouldNotAllowToGetNotUserProjectWithUsers() {
        var projectWithUsers = prepareProjectWithUsers();
        var projectId = projectWithUsers.project().id();
        var anotherUserId = UUID.randomUUID();

        Assertions.assertThatThrownBy(() -> service.get(projectId, anotherUserId))
                .isEqualTo(projectForbiddenException(anotherUserId, projectId));
    }

    static Stream<Project> invalidProjectCases() {
        var tooLogName = "a".repeat(FieldValidator.MAX_NAME_LENGTH + 1);

        return Stream.of(newProject(""),
                newProject(" "),
                newProject(null),
                newProject(tooLogName));
    }

    private static Project newProject(String name, long version) {
        return new Project(UUID.randomUUID(), UUID.randomUUID(), name, version);
    }

    private static Project newProject(String name) {
        return newProject(name, 0);
    }

    private Project projectWithAnotherOwnerIdAndIncreasedVersion(Project project, UUID ownerId) {
        return new Project(project.id(), ownerId, project.name(), project.version() + 1);
    }

    private AppResourceForbiddenException projectForbiddenException(UUID userId, UUID projectId) {
        return new AppResourceForbiddenException("%s user doesn't have access to %s project"
                .formatted(userId, projectId));
    }

    private ProjectWithUsers prepareProjectWithUsers() {
        var project = newProject("some project 3");
        projectRepository.save(project);

        var projectUsers = List.of(UUID.randomUUID(), UUID.randomUUID());
        projectUsersRepository.addUsers(project.id(), projectUsers);

        return new ProjectWithUsers(project, projectUsers);
    }
}
