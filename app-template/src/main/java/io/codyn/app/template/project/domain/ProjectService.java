package io.codyn.app.template.project.domain;

import io.codyn.app.template.project.domain.model.AddUsersToProjectCommand;
import io.codyn.app.template.project.domain.model.RemoveUsersFromProjectCommand;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectService {


    public void save(Project project, UUID userId) {
        //TODO: save project!!
    }

    public void delete(UUID projectId, UUID userId) {
        //TODO delete
    }

    public void addUsers(AddUsersToProjectCommand command) {
        //TODO add users
    }

    public void removeUsers(RemoveUsersFromProjectCommand command) {
        //TODO remove users
    }
}
