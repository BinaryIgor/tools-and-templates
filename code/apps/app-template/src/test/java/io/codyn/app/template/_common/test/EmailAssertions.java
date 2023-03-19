package io.codyn.app.template._common.test;

import io.codyn.app.template._common.core.model.ActivationTokenType;
import io.codyn.email.model.Email;
import io.codyn.email.model.EmailAddress;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class EmailAssertions {

    public static void toIsEqualTo(Email email, EmailAddress to) {
        Assertions.assertThat(email.to()).isEqualTo(to);
    }

    public static void messageContains(Email email, String... texts) {
        messageContains(email, List.of(texts));
    }

    public static void messageContains(Email email, List<String> texts) {
        Assertions.assertThat(email.textMessage()).contains(texts);
        Assertions.assertThat(email.htmlMessage()).contains(texts);
    }

    public static void tagIsEqualTo(Email email, String tag) {
        Assertions.assertThat(email.tag()).isEqualTo(tag);
    }

    public static void metadataContains(Email email, Map<String, String> metadata) {
        Assertions.assertThat(email.metadata()).isEqualTo(metadata);
    }

    public static void meetsExpectations(Email email, Expectations expectations) {
        if (Objects.nonNull(expectations.sentTo)) {
            toIsEqualTo(email, expectations.sentTo);
        }
        if (Objects.nonNull(expectations.messageContains)) {
            messageContains(email, expectations.messageContains);
        }
        if (Objects.nonNull(expectations.tagEqualTo)) {
            tagIsEqualTo(email, expectations.tagEqualTo);
        }
        if (Objects.nonNull(expectations.hasMetadata)) {
            metadataContains(email, expectations.hasMetadata);
        }
    }

    public static Expectations expectations() {
        return new Expectations();
    }

    public static class Expectations {
        private EmailAddress sentTo;
        private List<String> messageContains;
        private String tagEqualTo;
        private Map<String, String> hasMetadata;

        public Expectations sentTo(EmailAddress sentTo) {
            this.sentTo = sentTo;
            return this;
        }

        public Expectations sentTo(String name, String email) {
            return sentTo(new EmailAddress(name, email));
        }

        public Expectations messageContains(String... texts) {
            this.messageContains = List.of(texts);
            return this;
        }

        public Expectations tagIsEqual(String tag) {
            this.tagEqualTo = tag;
            return this;
        }

        public Expectations hasMetadata(Map<String, String> metadata) {
            this.hasMetadata = metadata;
            return this;
        }

        public Expectations hasUserIdActivationTokenMetadata(UUID userId,
                                                             ActivationTokenType activationTokenType) {
            return hasMetadata(Map.of("userId", userId.toString(),
                    "activationTokenType", activationTokenType.name()));
        }
    }
}
