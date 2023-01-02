package io.codyn.commons.test.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TestHttpServer {

    private final WireMockServer SERVER = new WireMockServer(WireMockConfiguration.wireMockConfig()
            .dynamicPort());

    public String baseUrl() {
        return SERVER.baseUrl();
    }

    public void start() {
        SERVER.start();
    }

    public void stop() {
        SERVER.stop();
    }

    public void reset() {
        SERVER.resetAll();
    }

    public ExpectationsBuilder expectations() {
        return new ExpectationsBuilder();
    }

    public record RequestExpectations(String method,
                                      String url,
                                      Map<String, String> headers,
                                      byte[] body) {
    }

    public record ResponseExpectations(int status,
                                       Map<String, String> headers,
                                       byte[] body) {
    }

    public class ExpectationsBuilder {

        private RequestExpectations request;
        private ResponseExpectations response;

        public ExpectationsBuilder request(RequestExpectations request) {
            this.request = request;
            return this;
        }

        public ExpectationsBuilder request(String method,
                                           String url,
                                           Map<String, String> headers,
                                           byte[] body) {
            return request(new RequestExpectations(method, url, headers, body));
        }

        public ExpectationsBuilder request(String method,
                                           String url,
                                           Map<String, String> headers,
                                           String body) {
            return request(method, url, headers, body.getBytes(StandardCharsets.UTF_8));
        }

        public ExpectationsBuilder request(String method,
                                           String url,
                                           Map<String, String> headers) {
            return request(method, url, headers, (byte[]) null);
        }

        public ExpectationsBuilder request(String method,
                                           String url) {
            return request(method, url, Map.of());
        }

        public ExpectationsBuilder response(ResponseExpectations response) {
            this.response = response;
            return this;
        }

        public ExpectationsBuilder response(int status,
                                            Map<String, String> headers,
                                            byte[] body) {
            return response(new ResponseExpectations(status, headers, body));
        }

        public ExpectationsBuilder response(int status,
                                            Map<String, String> headers,
                                            String body) {
            return response(status, headers, body.getBytes(StandardCharsets.UTF_8));
        }

        public ExpectationsBuilder response(int status,
                                            Map<String, String> headers) {
            return response(status, headers, (byte[]) null);
        }


        public ExpectationsBuilder response(int status) {
            return response(status, Map.of());
        }

        public void prepare() {
            if (request == null) {
                throw new RuntimeException("Request not set!");
            }
            if (response == null) {
                throw new RuntimeException("Response not set!");
            }

            var expectations = WireMock.request(request.method,
                    WireMock.urlMatching(request.url));

            if (request.body != null) {
                expectations.withRequestBody(WireMock.binaryEqualTo(request.body));
            }

            for (var h : request.headers().entrySet()) {
                expectations.withHeader(h.getKey(), WireMock.equalTo(h.getValue()));
            }

            var responseDefinition = WireMock.aResponse()
                    .withStatus(response.status);

            if (response.body != null) {
                responseDefinition.withBody(response.body);
            }

            for (var h : response.headers.entrySet()) {
                responseDefinition.withHeader(h.getKey(), h.getValue());
            }

            expectations.willReturn(responseDefinition);

            SERVER.stubFor(expectations);
        }

    }
}
