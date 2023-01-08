package io.codyn.app.template.project.app;

import io.codyn.app.template.SpringIntegrationTest;
import io.codyn.app.template._shared.app.IdResponse;
import io.codyn.app.template.project.app.model.ApiNewProject;
import io.codyn.app.template.project.app.model.ApiUpdateProject;
import io.codyn.app.template.project.domain.ProjectRepository;
import io.codyn.app.template.project.domain.model.Project;
import io.codyn.app.template.project.domain.model.ProjectWithUsers;
import io.codyn.app.template.user.domain.repository.NewUserRepository;
import io.codyn.commons.test.http.TestHttpClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectControllerTest extends SpringIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private NewUserRepository newUserRepository;

    @Test
    void shouldCreateNewProject() {
        var userId = UUID.randomUUID();

        var expectedProject = createNewProject(userId);

        Assertions.assertThat(projectRepository.findById(expectedProject.id()).orElseThrow())
                .isEqualTo(expectedProject);
    }

    @Test
    void shouldUpdateExistingProject() {
        var userId = UUID.randomUUID();
        var project = createNewProject(userId);

        var projectUpdate = new ApiUpdateProject(project.name() + "-new-name", project.version());

        updateRequest(project.id(), projectUpdate)
                .execute();

        var expectedProject = new Project(project.id(), project.ownerId(), projectUpdate.name(),
                project.version() + 1);

        Assertions.assertThat(projectRepository.findById(project.id()).orElseThrow())
                .isEqualTo(expectedProject);
    }

    @Test
    void shouldModifyProjectUsersAndReturnIt() {
        var userId = UUID.randomUUID();
        var project = createNewProject(userId);

        var toAddUsers = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        var toRemoveUsers = toAddUsers.subList(0, 2);

        toAddUsers.forEach(uid -> userClient.createRandomUser(uid));

        testHttpClient.test()
                .path("/projects/%s/users".formatted(project.id()))
                .POST()
                .body(toAddUsers)
                .execute();

        testHttpClient.test()
                .path("/projects/%s/users".formatted(project.id()))
                .DELETE()
                .body(toRemoveUsers)
                .execute();

        var expectedFirstUsers = new ArrayList<>(toAddUsers.subList(2, toAddUsers.size()));
        var expectedProjectWithUsers = new ProjectWithUsers(project, expectedFirstUsers);

        var projectWithUsers = fetchProjectWithUsers(project.id());

        Assertions.assertThat(projectWithUsers)
                .isEqualTo(expectedProjectWithUsers);
    }

    @Test
    void shouldDeleteExistingProject() {
        var project = createNewProject(UUID.randomUUID());

        Assertions.assertThat(projectRepository.findById(project.id())).isNotEmpty();

        testHttpClient.test()
                .path("/projects/" + project.id())
                .DELETE()
                .execute();

        Assertions.assertThat(projectRepository.findById(project.id())).isEmpty();
    }

    private Project createNewProject(UUID ownerId, ApiNewProject project) {
        userClient.createRandomUser(ownerId);
        setCurrentUser(ownerId);

        var projectId = testHttpClient.test()
                .path("/projects")
                .POST()
                .body(project)
                .expectedStatus(201)
                .executeReturningObject(IdResponse.class)
                .id();

        return new Project(projectId, ownerId, project.name(), 1);
    }

    private ProjectWithUsers fetchProjectWithUsers(UUID projectId) {
        return testHttpClient.test()
                .path("/projects/%s".formatted(projectId))
                .GET()
                .executeReturningObject(ProjectWithUsers.class);
    }

    private Project createNewProject(UUID ownerId) {
        return createNewProject(ownerId, new ApiNewProject("some-project"));
    }

    private TestHttpClient.TestBuilder updateRequest(UUID projectId, ApiUpdateProject projectUpdate) {
        return testHttpClient.test()
                .path("/projects/" + projectId)
                .PUT()
                .body(projectUpdate);
    }
}
