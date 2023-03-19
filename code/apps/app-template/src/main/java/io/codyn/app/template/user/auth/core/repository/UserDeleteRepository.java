package io.codyn.app.template.user.auth.core.repository;

import java.time.Instant;

public interface UserDeleteRepository {
    void deleteAllNotActivatedCreatedBefore(Instant before);
}
