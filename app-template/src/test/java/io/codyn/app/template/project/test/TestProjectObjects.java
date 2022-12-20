package io.codyn.app.template.project.test;

import io.codyn.app.template.project.domain.model.Project;
import io.codyn.app.template.test.Tests;

import java.util.UUID;

public class TestProjectObjects {

    public static Project newProject(long version) {
        return new Project(UUID.randomUUID(), UUID.randomUUID(), Tests.randomString(), version);
    }

    public static Project newProject() {
        return newProject(0);
    }
}
