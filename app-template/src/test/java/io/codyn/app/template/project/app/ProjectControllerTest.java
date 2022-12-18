package io.codyn.app.template.project.app;

import io.codyn.app.template.IntegrationTest;
import io.codyn.app.template._shared.app.IdResponse;
import io.codyn.app.template.project.app.model.ApiNewProject;
import io.codyn.app.template.project.domain.Project;
import io.codyn.app.template.project.domain.ProjectRepository;
import io.codyn.app.template.test.TestHttp;
import io.codyn.app.template.user.TestUserClient;
import io.codyn.app.template.user.api.CurrentUser;
import io.codyn.app.template.user.api.UserClient;
import io.codyn.app.template.user.domain.repository.NewUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

@Import(ProjectControllerTest.TestConfig.class)
public class ProjectControllerTest extends IntegrationTest {

    private static final CurrentUser CURRENT_USER = new CurrentUser(UUID.randomUUID());
    @Autowired
    private TestHttp testHttp;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private NewUserRepository newUserRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateNewProject() {
        var userId = TestUserClient.createUser(jdbcTemplate, CURRENT_USER.id());
        var project = new ApiNewProject("some-project");

        var response = testHttp.postAndExpectStatusReturningBody("/projects", project, HttpStatus.CREATED,
                IdResponse.class);
        var projectId = response.id();

        var expectedProject = new Project(projectId, userId, project.name(), 1);
        Assertions.assertThat(projectRepository.findById(projectId).orElseThrow())
                .isEqualTo(expectedProject);
    }


    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        TestUserClient userClient() {
            return new TestUserClient(CURRENT_USER);
        }
    }
}
