package io.codyn.app.template.project.app;

import io.codyn.app.template.IntegrationTest;
import io.codyn.app.template._shared.app.AppErrorResponse;
import io.codyn.app.template._shared.app.IdResponse;
import io.codyn.app.template._shared.domain.exception.AppOptimisticLockException;
import io.codyn.app.template._shared.domain.exception.AppResourceForbiddenException;
import io.codyn.app.template._shared.domain.exception.AppResourceNotFoundException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Import(ProjectControllerTest.TestConfig.class)
public class ProjectControllerTest extends IntegrationTest {

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

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE project.project CASCADE");
        userClient.clearDb();
    }

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

        var firstProject = createNewProject(userId);
        var secondProject = createNewProject(UUID.randomUUID(), new ApiNewProject("another-project"));

        var projectUpdate = new ApiUpdateProject(firstProject.name() + "-new-name", firstProject.version());

        userClient.setCurrentUser(userId);
        updateRequest(firstProject.id(), projectUpdate)
                .execute(HttpStatus.OK);

        var expectedProject = new Project(firstProject.id(), firstProject.ownerId(), projectUpdate.name(),
                firstProject.version() + 1);

        Assertions.assertThat(projectRepository.findById(firstProject.id()).orElseThrow())
                .isEqualTo(expectedProject);

        Assertions.assertThat(projectRepository.findById(secondProject.id()).orElseThrow())
                .isEqualTo(secondProject);
    }

    @Test
    void shouldNotAllowToUpdateNotUserProject() {
        var userId = UUID.randomUUID();

        var project = createNewProject(userId);

        var anotherUserId = userClient.setRandomCurrentUser();
        var expectedResponse = new AppErrorResponse(
                new AppResourceForbiddenException("%s user doesn't have access to %s project"
                        .formatted(anotherUserId, project.id())));

        var actualResponse = updateRequest(project.id(), new ApiUpdateProject("some-name", 1))
                .execute(HttpStatus.FORBIDDEN, AppErrorResponse.class);

        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturn409WhileUpdatingOutdatedProject() {
        var userId = UUID.randomUUID();

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

        var project = createNewProject(userId);

        var anotherUserId = userClient.setRandomCurrentUser();

        var projectUpdate = new ApiUpdateProject("new-project-name", 1);

        var response = updateRequest(project.id(), projectUpdate)
                .execute(HttpStatus.FORBIDDEN, AppErrorResponse.class);

        var expectedResponse = new AppErrorResponse(new AppResourceForbiddenException(
                "%s user doesn't have access to %s project".formatted(anotherUserId, project.id())));

        Assertions.assertThat(response).isEqualTo(expectedResponse);

        Assertions.assertThat(projectRepository.findById(project.id()).orElseThrow())
                .isEqualTo(project);
    }

    @Test
    void shouldModifyProjectUsersAndReturnIt() {
        var userId = UUID.randomUUID();

        var firstProject = createNewProject(userId);
        var secondProject = createNewProject(UUID.randomUUID(), new ApiNewProject("another-project"));

        userClient.setCurrentUser(userId);

        var toAddUsers = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        var toRemoveUsers = toAddUsers.subList(0, 2);

        toAddUsers.forEach(uid -> userClient.createRandomUser(uid));

        var toAddNextUser = UUID.randomUUID();
        userClient.createRandomUser(toAddNextUser);

        testHttp.builder()
                .path("/projects/%s/users".formatted(firstProject.id()))
                .method(HttpMethod.POST)
                .body(toAddUsers)
                .execute(HttpStatus.OK);

        testHttp.builder()
                .path("/projects/%s/users".formatted(firstProject.id()))
                .method(HttpMethod.DELETE)
                .body(toRemoveUsers)
                .execute(HttpStatus.OK);

        testHttp.builder()
                .path("/projects/%s/users".formatted(firstProject.id()))
                .method(HttpMethod.POST)
                .body(List.of(toAddNextUser))
                .execute(HttpStatus.OK);

        var expectedFirstUsers = new ArrayList<>(toAddUsers.subList(2, toAddUsers.size()));
        expectedFirstUsers.add(toAddNextUser);
        var firstProjectWithUsers = new ProjectWithUsers(firstProject, expectedFirstUsers);
        var secondProjectWithUsers = new ProjectWithUsers(secondProject, List.of());

        //TODO call api!
//        Assertions.assertThat(projectUsersRepository.findByIdWithUsers(firstProject.id()).orElseThrow())
//                .isEqualTo(firstProjectWithUsers);
//
//        Assertions.assertThat(projectRepository.findByIdWithUsers(secondProject.id()).orElseThrow())
//                .isEqualTo(secondProjectWithUsers);
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

    private Project createNewProject(UUID ownerId) {
        return createNewProject(ownerId, new ApiNewProject("some-project"));
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
