package io.codyn.app.template._common.core;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@Tag("integration")
public class AfterDelayCacheEnablerTest {

    @Test
    void shouldEnableCacheAfterDelayAndLeaveItEnabled() throws Exception {
        var enabler = new AfterDelayCacheEnabler(Duration.ofMillis(500));

        Assertions.assertThat(enabler.get()).isFalse();

        Thread.sleep(500);

        Assertions.assertThat(enabler.get()).isTrue();

        Thread.sleep(500);

        Assertions.assertThat(enabler.get()).isTrue();
    }
}
