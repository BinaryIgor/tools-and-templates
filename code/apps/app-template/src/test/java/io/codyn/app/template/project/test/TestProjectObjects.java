package io.codyn.app.template.project.test;

import io.codyn.app.template.project.core.model.Project;
import io.codyn.test.TestRandom;

import java.util.UUID;

public class TestProjectObjects {

    public static Project newProject(String name, long version) {
        return new Project(UUID.randomUUID(), UUID.randomUUID(), name, version);
    }

    public static Project newProject(String name) {
        return newProject(name, 0);
    }

    public static Project newProject(long version) {
        return newProject(TestRandom.name(), version);
    }

    public static Project newProject() {
        return newProject(0);
    }
}
