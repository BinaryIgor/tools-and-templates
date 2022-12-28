package io.codyn.app.template.project.infra;

import io.codyn.app.template.project.domain.model.Project;
import io.codyn.app.template.project.test.TestProjectObjects;
import io.codyn.app.template.user.TestUserClient;
import io.codyn.commons.sqldb.test.DbIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

public class SqlProjectUserRepositoryTest extends DbIntegrationTest {

    private SqlProjectUsersRepository repository;
    private SqlProjectRepository projectRepository;
    private TestUserClient userClient;

    @Override
    protected void setup() {
        repository = new SqlProjectUsersRepository(context);
        projectRepository = new SqlProjectRepository(context);
        userClient = new TestUserClient(context);
    }

    @Test
    void shouldModifyAndReturnUsersOfProject() {
        var firstProject = prepareNewProject();
        var secondProject = prepareNewProject();
        var firstProjectId = firstProject.id();
        var secondProjectId = secondProject.id();

        var firstToAddUsers = List.of(UUID.randomUUID(), UUID.randomUUID());
        var secondToAddUsers = List.of(UUID.randomUUID(), UUID.randomUUID());
        var toRemoveUsers = List.of(firstToAddUsers.get(0), secondToAddUsers.get(0));

        var expectedFirstProjectUsers = List.of(firstToAddUsers.get(1), secondToAddUsers.get(1));

        var secondProjectUsers = List.of(UUID.randomUUID(), UUID.randomUUID());

        firstToAddUsers.forEach(uid -> userClient.createRandomUser(uid));
        secondToAddUsers.forEach(uid -> userClient.createRandomUser(uid));
        secondProjectUsers.forEach(uid -> userClient.createRandomUser(uid));

        repository.addUsers(firstProjectId, firstToAddUsers);
        repository.addUsers(firstProjectId, secondToAddUsers);
        repository.removeUsers(firstProjectId, toRemoveUsers);

        repository.addUsers(secondProjectId, secondProjectUsers);

        Assertions.assertThat(repository.usersOfProject(firstProjectId))
                .isEqualTo(expectedFirstProjectUsers);

        Assertions.assertThat(repository.usersOfProject(secondProjectId))
                .isEqualTo(secondProjectUsers);
    }


    private Project prepareNewProject() {
        var project = TestProjectObjects.newProject(0);
        userClient.createRandomUser(project.ownerId());

        projectRepository.save(project);

        return project;
    }
}
