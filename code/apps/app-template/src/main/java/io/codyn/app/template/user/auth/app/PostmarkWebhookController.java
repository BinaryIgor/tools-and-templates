package io.codyn.app.template.user.auth.app;

import io.codyn.email.server.PostmarkEmailStatusHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PostmarkWebhookController {

    private static final String AUTH_HEADER = "postmark-webhook-token";
    private final PostmarkEmailStatusHandler postmarkEmailStatusHandler;

    public PostmarkWebhookController(PostmarkEmailStatusHandler postmarkEmailStatusHandler) {
        this.postmarkEmailStatusHandler = postmarkEmailStatusHandler;
    }

    @PostMapping("/webhooks/postmark")
    public void handle(@RequestHeader(AUTH_HEADER) String authToken,
                       @RequestBody Map<String, Object> record) {
        //TODO: validate header
        postmarkEmailStatusHandler.handle(record);
    }
}
