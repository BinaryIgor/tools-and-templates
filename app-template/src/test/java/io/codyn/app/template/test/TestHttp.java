package io.codyn.app.template.test;

import org.assertj.core.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;


public class TestHttp {

    private final TestRestTemplate template;

    public TestHttp(TestRestTemplate template) {
        this.template = template;
    }

    public RequestBuilder builder() {
        return new RequestBuilder(template);
    }

    public static class RequestBuilder {

        private final TestRestTemplate template;
        private String path;
        private Object body;
        private HttpMethod method;

        public RequestBuilder(TestRestTemplate restTemplate) {
            template = restTemplate;
        }

        public RequestBuilder path(String path) {
            this.path = path;
            return this;
        }

        public RequestBuilder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public RequestBuilder body(Object body) {
            this.body = body;
            return this;
        }

        public <T> T execute(HttpStatus expectedStatus, Class<T> responseType) {
            var entity = template.exchange(path, method, body == null ? null : new HttpEntity<>(body), responseType);

            Assertions.assertThat(entity.getStatusCode()).isEqualTo(expectedStatus);

            return entity.getBody();
        }

        public void execute(HttpStatus expectedStatus) {
            var entity = template.exchange(path, method,
                    body == null ? null : new HttpEntity<>(body), Void.class);

            Assertions.assertThat(entity.getStatusCode()).isEqualTo(expectedStatus);
        }
    }

}
