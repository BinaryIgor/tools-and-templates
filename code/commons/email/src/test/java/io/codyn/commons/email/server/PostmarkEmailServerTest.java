package io.codyn.commons.email.server;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.codyn.commons.email.model.Email;
import io.codyn.commons.email.model.EmailAddress;
import io.codyn.commons.json.JsonMapper;
import io.codyn.commons.test.TestRandom;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Tag("integration")
public class PostmarkEmailServerTest {

    private static final WireMockServer HTTP_SERVER = new WireMockServer(WireMockConfiguration.wireMockConfig()
            .dynamicPort());
    private static final Set<String> TO_CHECK_HEADERS = Set.of("content-type", "x-postmark-server-token");
    private PostmarkEmailServer emailServer;
    private String postmarkToken;

    @BeforeAll
    static void startHttpServer() {
        HTTP_SERVER.start();
    }

    @BeforeEach
    void setup() {
        postmarkToken = TestRandom.string();
        emailServer = new PostmarkEmailServer(HTTP_SERVER.baseUrl(), postmarkToken, 3, "outbound");
    }

    @AfterEach
    void tearDown() {
        HTTP_SERVER.resetAll();
    }

    @AfterAll
    static void stopHttpServer() {
        HTTP_SERVER.stop();
    }

    @Test
    void shouldSendEmailThroughApi() {
        var testCase = sendEmailTestCase();

        var expectations = WireMock.post(WireMock.urlPathEqualTo("/email"))
                .withRequestBody(WireMock.equalToJson(JsonMapper.json(testCase.expectedRequest.body)));



        for (var h : testCase.expectedRequest.headers.entrySet()) {
            expectations = expectations.withHeader(h.getKey(), WireMock.equalTo(h.getValue()));
        }

        expectations = expectations.willReturn(WireMock.aResponse()
                .withStatus(200));

        HTTP_SERVER.stubFor(expectations);

        emailServer.send(testCase.email);
    }

//    @Test
//    void send_withSendFailure_throwsSendEmailExceptionAfterRetrying() {
//        var code = TestsRandom.inRange(300, 500);
//        HTTP_SERVER.nextResponseCode(code);
//
//        Assertions.assertThatThrownBy(() -> emailServer.send(randomEmail()))
//                .isInstanceOf(EmailServer.EmailException.class);
//    }
//
//    @Test
//    void sendBatch_sendsEmailsThroughApi() {
//        var testCase = prepareSendEmailsTestCase();
//
//        emailServer.sendBatch(testCase.emails);
//
//        var actual = HTTP_SERVER.sentRequests(r -> {
//            var toCheckHeaders = Streams.filteredMap(r.headers(),
//                    h -> TO_CHECK_HEADERS.contains(h.getKey()));
//
//            return new EmailsRequestData(r.url(), r.method(), toCheckHeaders,
//                    JsonMapper.objects(r.bodyAsString(), PostmarkEmailServer.PostmarkEmail.class));
//        });
//
//        AssertThat.equals(actual, testCase.expectedRequests);
//    }
//
//    @Test
//    void sendBatch_withSendFailure_throwsSendEmailExceptionAfterRetrying() {
//        var code = TestsRandom.inRange(300, 500);
//        HTTP_SERVER.nextResponseCode(code);
//
//        var emails = Stream.generate(this::randomEmail)
//                .limit(5)
//                .toList();
//
//        Assertions.assertThatThrownBy(() -> emailServer.sendBatch(emails))
//                .isInstanceOf(EmailServer.EmailException.class);
//    }

    private SendEmailTestCase sendEmailTestCase() {
        var email = new Email(EmailAddress.ofNameEmail("Some App", "app@app.io"),
                EmailAddress.ofNameEmail("User", "user@user.io"),
                "Some email",
                "some html message",
                "some text message");

        var expectedRequest = new EmailRequestData(Map.of(
                "content-type", "application/json",
                "x-postmark-server-token", postmarkToken),
                new PostmarkEmailServer.PostmarkEmail(
                        "Some App <app@app.io>",
                        "User <user@user.io>",
                        "Some email",
                        "some html message",
                        "some text message",
                        "outbound"
                ));

        return new SendEmailTestCase(email, expectedRequest);
    }

//    private SendEmailsTestCase prepareSendEmailsTestCase() {
//        TestsDataExpressions.addVariable("postmarkUrl", HTTP_SERVER.baseUrl());
//        TestsDataExpressions.addVariable("token", postmarkToken);
//        return TestDataLoader.object("postmark_email_server/sendsBatchTestCase.json", SendEmailsTestCase.class);
//    }

    private Email randomEmail() {
        return new Email(EmailAddress.ofEmptyName(TestRandom.name()), EmailAddress.ofEmptyName(TestRandom.name()),
                TestRandom.string(), TestRandom.string(), TestRandom.string());
    }

    private record SendEmailTestCase(Email email, EmailRequestData expectedRequest) {
    }

    private record SendEmailsTestCase(List<Email> emails, List<EmailsRequestData> expectedRequests) {
    }

    private record EmailRequestData(Map<String, String> headers,
                                    PostmarkEmailServer.PostmarkEmail body) {
    }


    private record EmailsRequestData(String url,
                                     String method,
                                     Map<String, String> headers,
                                     List<PostmarkEmailServer.PostmarkEmail> body) {
    }
}
