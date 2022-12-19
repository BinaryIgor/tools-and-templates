package io.codyn.app.template.project.app;

import io.codyn.app.template.IntegrationTest;
import io.codyn.app.template._shared.app.AppErrorResponse;
import io.codyn.app.template._shared.app.IdResponse;
import io.codyn.app.template._shared.domain.exception.AppOptimisticLockException;
import io.codyn.app.template._shared.domain.exception.AppResourceForbiddenException;
import io.codyn.app.template._shared.domain.exception.AppResourceNotFoundException;
import io.codyn.app.template.project.app.model.ApiNewProject;
import io.codyn.app.template.project.app.model.ApiUpdateProject;
import io.codyn.app.template.project.domain.Project;
import io.codyn.app.template.project.domain.ProjectRepository;
import io.codyn.app.template.test.TestHttp;
import io.codyn.app.template.user.TestUserClient;
import io.codyn.app.template.user.domain.repository.NewUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

@Import(ProjectControllerTest.TestConfig.class)
public class ProjectControllerTest extends IntegrationTest {

    @Autowired
    private TestHttp testHttp;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private NewUserRepository newUserRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TestUserClient userClient;

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE project.project CASCADE");
        userClient.clearDb();
    }

    @Test
    void shouldCreateNewProject() {
        var userId = UUID.randomUUID();
        userClient.setCurrentUser(userId);

        var expectedProject = createNewProject(userId);

        Assertions.assertThat(projectRepository.findById(expectedProject.id()).orElseThrow())
                .isEqualTo(expectedProject);
    }

    @Test
    void shouldUpdateExistingProject() {
        var userId = UUID.randomUUID();
        userClient.setCurrentUser(userId);

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
    void shouldReturn409WhileUpdatingOutdatedProject() {
        var userId = userClient.setRandomCurrentUser();

        var project = createNewProject(userId);

        var projectUpdate = new ApiUpdateProject("updated name", 1);

        updateRequest(project.id(), projectUpdate)
                .execute(HttpStatus.OK);

        var errorResponse = updateRequest(project.id(), projectUpdate)
                .execute(HttpStatus.CONFLICT, AppErrorResponse.class);

        var expectedErrorResponse = new AppErrorResponse(new AppOptimisticLockException());

        Assertions.assertThat(errorResponse)
                .isEqualTo(expectedErrorResponse);
    }

    @Test
    void shouldReturn404WhileUpdatingNonExistingProject() {
        var projectId = UUID.randomUUID();
        var projectUpdate = new ApiUpdateProject("some-name", 1);

        var response = updateRequest(projectId, projectUpdate)
                .execute(HttpStatus.NOT_FOUND, AppErrorResponse.class);

        var expectedResponse = new AppErrorResponse(new AppResourceNotFoundException(
                "There is no project of %s id".formatted(projectId)));

        Assertions.assertThat(response).isEqualTo(expectedResponse);

        Assertions.assertThat(projectRepository.findById(projectId)).isEmpty();
    }

    @Test
    void shouldReturn403WhileUpdatingNotUserProject() {
        var userId = UUID.randomUUID();
        userClient.setCurrentUser(userId);

        var project = createNewProject(userId);

        var anotherUserId = userClient.createRandomUser(UUID.randomUUID());
        userClient.setCurrentUser(anotherUserId);

        var projectUpdate = new ApiUpdateProject("new-project-name", 1);

        var response = updateRequest(project.id(), projectUpdate)
                .execute(HttpStatus.FORBIDDEN, AppErrorResponse.class);

        var expectedResponse = new AppErrorResponse(new AppResourceForbiddenException(
                "%s user doesn't have access to %s project".formatted(anotherUserId, project.id())));

        Assertions.assertThat(response).isEqualTo(expectedResponse);

        Assertions.assertThat(projectRepository.findById(project.id()).orElseThrow())
                .isEqualTo(project);
    }

    private Project createNewProject(UUID ownerId) {
        userClient.createRandomUser(ownerId);
        var project = new ApiNewProject("some-project");

        var projectId = testHttp.builder()
                .path("/projects")
                .method(HttpMethod.POST)
                .body(project)
                .execute(HttpStatus.CREATED, IdResponse.class)
                .id();

        return new Project(projectId, ownerId, project.name(), 1);
    }

    private TestHttp.RequestBuilder updateRequest(UUID projectId, ApiUpdateProject projectUpdate) {
        return testHttp.builder()
                .path("/projects/" + projectId)
                .method(HttpMethod.PUT)
                .body(projectUpdate);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        TestUserClient userClient(JdbcTemplate jdbcTemplate) {
            return new TestUserClient(jdbcTemplate);
        }
    }
}
