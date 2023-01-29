package io.codyn.test.http;

import io.codyn.json.JsonMapper;
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
    private final Map<String, String> globalHeaders = new HashMap<>();

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

    public void addHeader(String name, String value) {
        globalHeaders.put(name, value);
    }

    public void addBearerAuthorizationHeader(String token) {
        addHeader("Authorization", "Bearer " + token);
    }

    public TestBuilder test() {
        return new TestBuilder();
    }

    public class TestBuilder {

        private final Map<String, List<String>> headers = new HashMap<>();
        private String path;
        private Object body;
        private String method;

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

        public Response execute() {
            try {
                var response = httpClient.send(request(), HttpResponse.BodyHandlers.ofString());
                return new Response(response.statusCode(), response.body());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private HttpRequest request() {
            var request = HttpRequest.newBuilder()
                    .uri(requestUri())
                    .method(method, requestBodyPublisher())
                    .timeout(Duration.ofSeconds(5));

            globalHeaders.forEach(request::header);

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

    public static class Response {
        final int statusCode;
        final String body;

        public Response(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }

        public Response expectStatus(int expectedStatus) {
            Assertions.assertEquals(expectedStatus, statusCode,
                    "Expected status: %d, but was %d. Response body: %s"
                            .formatted(expectedStatus, statusCode, body));
            return this;
        }

        public Response expectOkStatus() {
            return expectStatus(200);
        }

        public Response expectCreatedStatus() {
            return expectStatus(201);
        }

        public Response expectBadRequestStatus() {
            return expectStatus(400);
        }

        public Response expectNotFoundStatus() {
            return expectStatus(404);
        }

        public <T> T expectObjectBody(Class<T> type) {
            return JsonMapper.object(body, type);
        }

        public <T> List<T> expectObjectsBody(Class<T> type) {
            return JsonMapper.objects(body, type);
        }
    }

}
