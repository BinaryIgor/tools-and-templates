package io.codyn.app.template.project.test;

import io.codyn.app.template.project.domain.ProjectUsersRepository;

import java.util.*;

public class FakeProjectUsersRepository implements ProjectUsersRepository {

    private final Map<UUID, List<UUID>> usersOfProjects = new HashMap<>();

    @Override
    public List<UUID> usersOfProject(UUID id) {
        return usersOfProjects.getOrDefault(id, List.of());
    }

    @Override
    public void addUsers(UUID id, List<UUID> userIds) {
        usersOfProjects.put(id, userIds);
    }

    @Override
    public void removeUsers(UUID id, List<UUID> userIds) {
        usersOfProjects.getOrDefault(id, new ArrayList<>())
                .removeIf(userIds::contains);
    }
}
