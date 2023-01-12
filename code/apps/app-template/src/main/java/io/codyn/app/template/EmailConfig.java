package io.codyn.app.template;

import io.codyn.commons.email.model.EmailAddress;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.email")
public record EmailConfig(boolean fakeServer,
                          String templatesDir,
                          String postmarkApiToken,
                          //TODO: use it
                          String postmarkWebhookToken,
                          String frontendDomain,
                          EmailAddress fromEmail) {
}
