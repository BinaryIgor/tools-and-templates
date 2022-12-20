package io.codyn.app.template.project.domain;

import java.util.List;
import java.util.UUID;

public interface ProjectUsersRepository {

    List<UUID> usersOfProject(UUID id);

    void addUsers(UUID id, List<UUID> userIds);

    void removeUsers(UUID id, List<UUID> userIds);
}
