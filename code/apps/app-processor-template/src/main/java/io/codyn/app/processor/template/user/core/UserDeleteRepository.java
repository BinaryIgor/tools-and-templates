package io.codyn.app.processor.template.user.core;

import java.time.Instant;

public interface UserDeleteRepository {
    void deleteAllNotActivatedCreatedBefore(Instant before);
}
