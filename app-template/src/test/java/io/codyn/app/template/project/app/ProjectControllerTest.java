package io.codyn.app.template.project.app;

import io.codyn.app.template.SpringIntegrationTest;
import io.codyn.app.template._shared.app.IdResponse;
import io.codyn.app.template.project.app.model.ApiNewProject;
import io.codyn.app.template.project.app.model.ApiUpdateProject;
import io.codyn.app.template.project.domain.ProjectRepository;
import io.codyn.app.template.project.domain.ProjectUsersRepository;
import io.codyn.app.template.project.domain.model.Project;
import io.codyn.app.template.project.domain.model.ProjectWithUsers;
import io.codyn.app.template.test.TestHttp;
import io.codyn.app.template.user.TestUserClient;
import io.codyn.app.template.user.domain.repository.NewUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectControllerTest extends SpringIntegrationTest {

    @Autowired
    private TestHttp testHttp;
    @Autowired
    private ProjectRepository projectRepository;
    private ProjectUsersRepository projectUsersRepository;
    @Autowired
    private NewUserRepository newUserRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TestUserClient userClient;

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
                .execute(HttpStatus.OK);

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

        testHttp.builder()
                .path("/projects/%s/users".formatted(project.id()))
                .method(HttpMethod.POST)
                .body(toAddUsers)
                .execute(HttpStatus.OK);

        testHttp.builder()
                .path("/projects/%s/users".formatted(project.id()))
                .method(HttpMethod.DELETE)
                .body(toRemoveUsers)
                .execute(HttpStatus.OK);

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

        testHttp.builder()
                .path("/projects/" + project.id())
                .method(HttpMethod.DELETE)
                .execute(HttpStatus.OK);

        Assertions.assertThat(projectRepository.findById(project.id())).isEmpty();
    }

    private Project createNewProject(UUID ownerId, ApiNewProject project) {
        userClient.setCurrentUser(ownerId);
        userClient.createRandomUser(ownerId);

        var projectId = testHttp.builder()
                .path("/projects")
                .method(HttpMethod.POST)
                .body(project)
                .execute(HttpStatus.CREATED, IdResponse.class)
                .id();

        return new Project(projectId, ownerId, project.name(), 1);
    }

    private ProjectWithUsers fetchProjectWithUsers(UUID projectId) {
        return testHttp.builder()
                .path("/projects/%s".formatted(projectId))
                .method(HttpMethod.GET)
                .execute(HttpStatus.OK, ProjectWithUsers.class);
    }

    private Project createNewProject(UUID ownerId) {
        return createNewProject(ownerId, new ApiNewProject("some-project"));
    }

    private TestHttp.RequestBuilder updateRequest(UUID projectId, ApiUpdateProject projectUpdate) {
        return testHttp.builder()
                .path("/projects/" + projectId)
                .method(HttpMethod.PUT)
                .body(projectUpdate);
    }
}
