package io.codyn.app.template.user.core.component;

import io.codyn.app.template._shared.core.email.Emails;
import io.codyn.app.template.user.core.model.EmailUser;
import io.codyn.app.template.user.core.model.activation.ActivationTokenType;
import io.codyn.email.factory.EmailFactory;
import io.codyn.email.model.EmailAddress;
import io.codyn.email.model.NewEmailTemplate;
import io.codyn.email.server.EmailServer;

import java.util.Map;

//TODO: test, config
public class UserEmailComponent {

    private final EmailFactory factory;
    private final EmailServer server;
    private final Config config;

    public UserEmailComponent(EmailFactory factory, EmailServer server, Config config) {
        this.factory = factory;
        this.server = server;
        this.config = config;
    }

    public void sendAccountActivation(EmailUser user, String activationToken) {
        var variables = Map.of(Emails.Variables.USER, user.name(),
                Emails.Variables.ACTIVATION_URL,
                fullTokenUrl(config.userActivationUrl(), activationToken, ActivationTokenType.NEW_USER),
                Emails.Variables.SIGN_UP_URL, fullUrl(config.signUpUrl()));

        sendEmail(user, Emails.Types.USER_ACTIVATION, variables);
    }

    private String fullTokenUrl(String endpoint, String token, ActivationTokenType type) {
        var url = String.join("/", config.frontendDomain(), endpoint);
        var params = "token=%s&type=%s".formatted(token, type);
        return url + "?" + params;
    }

    private String fullUrl(String part) {
        return String.join("/", config.frontendDomain(), part);
    }


    private void sendEmail(EmailUser user, String type, Map<String, String> variables) {
        var emailTemplate = new NewEmailTemplate(config.fromEmail(),
                EmailAddress.ofNameEmail(user.name(), user.email()),
                user.language().name(),
                type,
                variables);

        var email = factory.newEmail(emailTemplate);

        server.send(email);
    }

    public record Config(
            String frontendDomain,
            EmailAddress fromEmail,
            String userActivationUrl,
            String signUpUrl,
            String emailChangeConfirmationUrl,
            String passwordResetUrl,
            String newPasswordUrl) {
    }
}
