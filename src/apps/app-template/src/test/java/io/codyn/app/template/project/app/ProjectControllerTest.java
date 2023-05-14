package io.codyn.app.template.project.app;

import io.codyn.app.template.SpringIntegrationTest;
import io.codyn.app.template.project.app.model.ApiNewProject;
import io.codyn.app.template.project.app.model.ApiUpdateProject;
import io.codyn.app.template.project.core.model.Project;
import io.codyn.app.template.project.core.model.ProjectWithUsers;
import io.codyn.test.http.TestHttpClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectControllerTest extends SpringIntegrationTest {


    @Test
    void shouldCreateNewProject() {
        var userId = UUID.randomUUID();

        var expectedProject = createNewProject(userId);

        Assertions.assertThat(fetchProject(expectedProject.id()))
                .isEqualTo(expectedProject);
    }

    @Test
    void shouldUpdateExistingProject() {
        var userId = UUID.randomUUID();
        var project = createNewProject(userId);

        var projectUpdate = new ApiUpdateProject(project.name() + "-new-name", project.version());

        updateRequest(project.id(), projectUpdate)
                .expectStatusOk();

        var expectedProject = new Project(project.id(), project.ownerId(), projectUpdate.name(),
                project.version() + 1);

        Assertions.assertThat(fetchProject(expectedProject.id()))
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
                .path(projectsPath("%s/users".formatted(project.id())))
                .POST()
                .body(toAddUsers)
                .execute()
                .expectStatusOk();

        testHttpClient.test()
                .path(projectsPath("%s/users".formatted(project.id())))
                .DELETE()
                .body(toRemoveUsers)
                .execute()
                .expectStatusOk();

        var expectedFirstUsers = new ArrayList<>(toAddUsers.subList(2, toAddUsers.size()));
        var expectedProjectWithUsers = new ProjectWithUsers(project, expectedFirstUsers);

        var projectWithUsers = fetchProjectWithUsers(project.id());

        Assertions.assertThat(projectWithUsers)
                .isEqualTo(expectedProjectWithUsers);
    }

    @Test
    void shouldDeleteExistingProject() {
        var project = createNewProject(UUID.randomUUID());

        testHttpClient.test()
                .path(projectPath(project.id()))
                .GET()
                .execute()
                .expectStatusOk();

        testHttpClient.test()
                .path(projectPath(project.id()))
                .DELETE()
                .execute()
                .expectStatusOk();

        testHttpClient.test()
                .path(projectPath(project.id()))
                .GET()
                .execute()
                .expectStatusNotFound();
    }

    private Project createNewProject(UUID ownerId, ApiNewProject project) {
        createUserAndSetAuthToken(ownerId);

        return testHttpClient.test()
                .path("/projects")
                .POST()
                .body(project)
                .execute()
                .expectStatusCreated()
                .expectBodyOfObject(Project.class);
    }

    private ProjectWithUsers fetchProjectWithUsers(UUID projectId) {
        return testHttpClient.test()
                .path(projectPath(projectId))
                .GET()
                .execute()
                .expectStatusOk()
                .expectBodyOfObject(ProjectWithUsers.class);
    }

    private Project fetchProject(UUID projectId) {
        return fetchProjectWithUsers(projectId).project();
    }

    private Project createNewProject(UUID ownerId) {
        return createNewProject(ownerId, new ApiNewProject("some-project"));
    }

    private TestHttpClient.Response updateRequest(UUID projectId, ApiUpdateProject projectUpdate) {
        return testHttpClient.test()
                .path(projectPath(projectId))
                .PUT()
                .body(projectUpdate)
                .execute();
    }

    private String projectsPath(String path) {
        return "/projects/" + path;
    }

    private String projectPath(UUID projectId) {
        return projectsPath(projectId.toString());
    }
}
