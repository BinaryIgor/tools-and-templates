package io.codyn.app.template.project.test;

import io.codyn.app.template.project.core.model.Project;
import io.codyn.test.TestRandom;

import java.util.UUID;

public class TestProjectObjects {

    public static Project newProject(long version) {
        return new Project(UUID.randomUUID(), UUID.randomUUID(), TestRandom.string(), version);
    }

    public static Project newProject() {
        return newProject(0);
    }
}
