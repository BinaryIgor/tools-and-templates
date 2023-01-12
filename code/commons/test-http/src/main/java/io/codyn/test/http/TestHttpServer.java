package io.codyn.test.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.matching.UrlPattern;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TestHttpServer {

    private final WireMockServer server = new WireMockServer(WireMockConfiguration.wireMockConfig()
            .dynamicPort());
    private final AtomicInteger requestsCount = new AtomicInteger(0);

    public TestHttpServer() {
        server.addMockServiceRequestListener((req, res) -> {
            requestsCount.incrementAndGet();
        });
    }

    public String baseUrl() {
        return server.baseUrl();
    }

    public int requestsCount() {
        return requestsCount.get();
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop();
    }

    public void reset() {
        requestsCount.set(0);
        server.resetAll();
    }

    public void expectResponseStatus(int status) {
        expectResponse(status, Map.of(), (byte[]) null);
    }

    public void expectResponse(int status,
                               Map<String, String> headers,
                               byte[] body) {
        server.stubFor(WireMock.any(UrlPattern.ANY)
                .willReturn(aResponse(status, headers, body, null)));
    }

    public void expectResponse(int status,
                               String body) {
        expectResponse(status, Map.of(), body);
    }

    public void expectResponse(int status,
                               Map<String, String> headers,
                               String body) {
        server.stubFor(WireMock.any(UrlPattern.ANY)
                .willReturn(aResponse(status, headers, null, body)));
    }

    private ResponseDefinitionBuilder aResponse(int status,
                                                Map<String, String> headers,
                                                byte[] body,
                                                String txtBody) {
        var response = WireMock.aResponse()
                .withStatus(status)
                .withHeaders(mapToHttpHeaders(headers));

        if (body != null) {
            response.withBody(body);
        } else if (txtBody != null) {
            response.withBody(txtBody);
        }

        return response;
    }

    private HttpHeaders mapToHttpHeaders(Map<String, String> map) {
        var headers = new ArrayList<HttpHeader>();
        map.forEach((k, v) -> headers.add(new HttpHeader(k, v)));
        return new HttpHeaders(headers);
    }

    public ExpectationsBuilder expectations() {
        return new ExpectationsBuilder();
    }

    public record RequestExpectations(String method,
                                      String url,
                                      Map<String, String> headers,
                                      byte[] body,
                                      String txtBody) {
    }

    public record ResponseExpectations(int status,
                                       Map<String, String> headers,
                                       byte[] body,
                                       String txtBody) {
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
            return request(new RequestExpectations(method, url, headers, body, null));
        }

        public ExpectationsBuilder request(String method,
                                           String url,
                                           Map<String, String> headers,
                                           String body) {
            return request(new RequestExpectations(method, url, headers, null, body));
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
            return response(new ResponseExpectations(status, headers, body, null));
        }

        public ExpectationsBuilder response(int status,
                                            Map<String, String> headers,
                                            String body) {
            return response(new ResponseExpectations(status, headers, null, body));
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
                response(200);
            }

            var expectations = WireMock.request(request.method,
                    WireMock.urlMatching(request.url));

            if (request.body != null) {
                expectations.withRequestBody(WireMock.binaryEqualTo(request.body));
            } else if (request.txtBody != null) {
                expectations.withRequestBody(WireMock.equalTo(request.txtBody));
            }

            for (var h : request.headers().entrySet()) {
                expectations.withHeader(h.getKey(), WireMock.equalTo(h.getValue()));
            }

            expectations.willReturn(aResponse(response.status, response.headers, response.body, request.txtBody));

            server.stubFor(expectations);
        }
    }
}
