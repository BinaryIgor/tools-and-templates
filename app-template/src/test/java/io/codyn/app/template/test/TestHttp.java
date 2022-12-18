package io.codyn.app.template.test;

import org.assertj.core.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import java.util.function.Consumer;

public class TestHttp {

    private final TestRestTemplate template;

    public TestHttp(TestRestTemplate template) {
        this.template = template;
    }

    public void postAndExpectStatus(String path, Object body, HttpStatus status) {
        Assertions.assertThat(template.postForEntity(path, body, null).getStatusCode())
                .isEqualTo(status);
    }

    public <T> T postAndExpectStatusReturningBody(String path, Object body,
                                                  HttpStatus status,
                                                  Class<T> bodyClazz) {
        var entity = template.postForEntity(path, body, bodyClazz);

        Assertions.assertThat(entity.getStatusCode())
                .isEqualTo(status);

        return entity.getBody();
    }

    public <T> void getAndExpectOkStatusAndBody(String path, Class<T> clazz, Consumer<T> expectation) {
        var result = template.getForEntity(path, clazz);
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        expectation.accept(result.getBody());
    }
}
