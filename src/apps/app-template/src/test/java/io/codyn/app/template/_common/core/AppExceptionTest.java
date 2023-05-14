package io.codyn.app.template._common.core;

import io.codyn.app.template._common.core.exception.AppException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AppExceptionTest {

    @Test
    void shouldReturnOneErrorWithoutExceptionSuffix() {
        var someException = new CustomTestException();
        Assertions.assertThat(someException.toErrors())
                .isEqualTo(List.of("CustomTest"));
    }

    public static class CustomTestException extends AppException {
        public CustomTestException() {
            super("Some error");
        }
    }
}
