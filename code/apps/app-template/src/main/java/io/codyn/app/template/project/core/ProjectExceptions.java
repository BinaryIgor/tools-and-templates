package io.codyn.app.template.project.core;

import io.codyn.app.template._common.core.exception.AccessForbiddenException;
import io.codyn.app.template._common.core.exception.NotFoundException;

import java.util.UUID;

public class ProjectExceptions {

    public static NotFoundException projectNotFoundException(UUID projectId) {
        return new NotFoundException("There is no project of %s id".formatted(projectId));
    }

    public static AccessForbiddenException projectForbiddenException(UUID userId, UUID projectId) {
        return new AccessForbiddenException("%s user doesn't have access to %s project"
                .formatted(userId, projectId));
    }
}
