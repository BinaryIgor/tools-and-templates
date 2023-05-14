package io.codyn.test.http;

import io.codyn.json.JsonMapper;
import org.junit.jupiter.api.Assertions;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;


public class TestHttpClient {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String COOKIE_HEADER = "Cookie";
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

    public void removeHeader(String name) {
        globalHeaders.remove(name);
    }

    public void addBearerAuthorizationHeader(String token) {
        addHeader(AUTHORIZATION_HEADER, "Bearer " + token);
    }

    public void addCookie(String key, String value) {
        addHeader(COOKIE_HEADER, "%s=%s".formatted(key, value));
    }

    public void removeAuthorizationHeader() {
        removeHeader(AUTHORIZATION_HEADER);
    }

    public void removeCookies() {
        removeHeader(COOKIE_HEADER);
    }

    public TestBuilder test() {
        return new TestBuilder();
    }

    public record Response(int statusCode, HttpHeaders headers, byte[] body) {

        public String bodyAsString() {
            return new String(body, StandardCharsets.UTF_8);
        }

        public Response expectStatus(int expectedStatus) {
            Assertions.assertEquals(expectedStatus, statusCode,
                    "Expected status: %d, but was %d. Response body: %s"
                            .formatted(expectedStatus, statusCode, body));
            return this;
        }

        public Response expectStatusOk() {
            return expectStatus(200);
        }

        public Response expectStatusCreated() {
            return expectStatus(201);
        }

        public Response expectStatusBadRequest() {
            return expectStatus(400);
        }

        public Response expectStatusNotFound() {
            return expectStatus(404);
        }

        public Response expectHeader(String key, String... values) {
            var actualValues = headers.allValues(key.toLowerCase());

            for (var value : values) {
                Assertions.assertTrue(actualValues.stream().anyMatch(v -> v.equals(value)),
                        "There is no header of %s key with the %s value".formatted(key, value));
            }

            return this;
        }

        public <T> T expectBodyOfObject(Class<T> type) {
            return JsonMapper.object(bodyAsString(), type);
        }

        public <T> List<T> expectBodyOfObjects(Class<T> type) {
            return JsonMapper.objects(bodyAsString(), type);
        }

        public <T> T expectBody(Function<String, T> mapper) {
            return mapper.apply(bodyAsString());
        }
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

        //TODO: refactor
        public TestBuilder queryParam(String key, String value) {
            var keyValue = "%s=%s".formatted(key, value);
            if (path.contains("?")) {
                path += "&" + keyValue;
            } else {
                path += "?" + keyValue;
            }
            return this;
        }

        public TestBuilder joinedPath(String... paths) {
            return path(String.join("/", paths));
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
                var response = httpClient.send(request(), HttpResponse.BodyHandlers.ofByteArray());
                return new Response(response.statusCode(), response.headers(), response.body());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private HttpRequest request() {
            validateRequiredFieldsAreSet();

            var request = HttpRequest.newBuilder()
                    .uri(requestUri())
                    .method(method, requestBodyPublisher())
                    .timeout(Duration.ofSeconds(5));

            globalHeaders.forEach(request::header);

            headers.forEach((k, vs) ->
                    vs.forEach(v -> request.header(k, v)));

            return request.build();
        }

        private void validateRequiredFieldsAreSet() {
            if (method == null) {
                throw new IllegalStateException("Method is not set, but is required");
            }
            if (path == null) {
                throw new IllegalStateException("Path is not set, but is required");
            }
        }

        private URI requestUri() {
            try {
                var path = this.path;
                if (!path.startsWith("/")) {
                    path = "/" + path;
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
