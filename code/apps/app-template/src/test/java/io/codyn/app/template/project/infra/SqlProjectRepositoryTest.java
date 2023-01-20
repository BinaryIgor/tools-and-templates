package io.codyn.app.template.project.infra;

import io.codyn.app.template.project.core.model.Project;
import io.codyn.app.template.project.test.TestProjectObjects;
import io.codyn.app.template.user.TestUserClient;
import io.codyn.sqldb.test.DbIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SqlProjectRepositoryTest extends DbIntegrationTest {

    private SqlProjectRepository repository;
    private TestUserClient userClient;

    @Override
    protected void setup() {
        repository = new SqlProjectRepository(context);
        userClient = new TestUserClient(contextProvider);
    }

    @Test
    void shouldFindOwnerById() {
        var firstProject = prepareNewProject();
        var secondProject = prepareNewProject();

        repository.save(firstProject);
        repository.save(secondProject);

        Assertions.assertThat(repository.findOwnerById(firstProject.id()).orElseThrow())
                .isEqualTo(firstProject.ownerId());

        Assertions.assertThat(repository.findOwnerById(secondProject.id()).orElseThrow())
                .isEqualTo(secondProject.ownerId());
    }

    @Test
    void shouldDeleteProjectById() {
        var firstProject = prepareNewProject();
        var secondProject = prepareNewProject();

        repository.save(firstProject);
        repository.save(secondProject);

        repository.delete(firstProject.id());

        Assertions.assertThat(repository.findById(firstProject.id())).isEmpty();

        Assertions.assertThat(repository.findById(secondProject.id())).isNotEmpty();
    }

    private Project prepareNewProject() {
        var project = TestProjectObjects.newProject(0);
        userClient.createRandomUser(project.ownerId());
        return project;
    }

}
