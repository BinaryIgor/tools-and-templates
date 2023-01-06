package io.codyn.commons.test.http;

import io.codyn.commons.json.JsonMapper;
import org.junit.jupiter.api.Assertions;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


public class TestHttpClient {

    private final HttpClient httpClient;
    private final Supplier<String> baseUrl;

    public TestHttpClient(HttpClient httpClient,
                          Supplier<String> baseUrl) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
    }

    public TestHttpClient(String baseUrl) {
        this(HttpClient.newBuilder().build(), () -> baseUrl);
    }

    public TestHttpClient(Supplier<Integer> port) {
        this(HttpClient.newBuilder().build(), () -> "http://localhost:" + port.get());
    }

    public TestBuilder test() {
        return new TestBuilder();
    }

    public class TestBuilder {

        private final Map<String, List<String>> headers = new HashMap<>();
        private String path;
        private Object body;
        private String method;
        private int expectedStatus = 200;

        public TestBuilder path(String path) {
            this.path = path;
            return this;
        }

        public TestBuilder method(String method) {
            this.method = method;
            return this;
        }

        public TestBuilder header(String key, String... values) {
            headers.computeIfAbsent(key, k -> new ArrayList<>()).addAll(List.of(values));
            return this;
        }

        public TestBuilder headers(Map<String, List<String>> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public TestBuilder expectedStatus(int expectedStatus) {
            this.expectedStatus = expectedStatus;
            return this;
        }

        public TestBuilder GET() {
            return method("GET");
        }

        public TestBuilder POST() {
            return method("POST");
        }

        public TestBuilder PUT() {
            return method("PUT");
        }

        public TestBuilder PATCH() {
            return method("PATCH");
        }

        public TestBuilder DELETE() {
            return method("DELETE");
        }

        public TestBuilder body(Object body) {
            this.body = body;
            return header("content-type", "application/json");
        }

        public <T> T executeReturningObject(Class<T> type) {
            var json = execute();
            return JsonMapper.object(json, type);
        }

        public <T> List<T> executeReturningObjects(Class<T> type) {
            var json = execute();
            return JsonMapper.objects(json, type);
        }

        public String execute() {
            try {
                var response = httpClient.send(request(), HttpResponse.BodyHandlers.ofString());
                var body = response.body();
                Assertions.assertEquals(expectedStatus, response.statusCode(),
                        "Expected status: %d, but was %d. Response body: %s"
                                .formatted(expectedStatus, response.statusCode(), body));
                return body;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void executeIgnoringBody() {
            try {
                var response = httpClient.send(request(), HttpResponse.BodyHandlers.discarding());
                Assertions.assertEquals(expectedStatus, response.statusCode());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private HttpRequest request() {
            var request = HttpRequest.newBuilder()
                    .uri(requestUri())
                    .method(method, requestBodyPublisher())
                    .timeout(Duration.ofSeconds(1));

            headers.forEach((k, vs) ->
                    vs.forEach(v -> request.header(k, v)));

            return request.build();
        }

        private URI requestUri() {
            try {
                var path = this.path;
                if (!path.startsWith("/")) {
                    path += "/";
                }
                return new URI(baseUrl.get() + path);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private HttpRequest.BodyPublisher requestBodyPublisher() {
            if (body == null) {
                return HttpRequest.BodyPublishers.noBody();
            }
            return HttpRequest.BodyPublishers.ofString(JsonMapper.json(body));
        }
    }

}
