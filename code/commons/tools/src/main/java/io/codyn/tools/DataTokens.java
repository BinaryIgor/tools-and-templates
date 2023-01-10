package io.codyn.tools;

import java.util.Optional;
import java.util.UUID;

public class DataTokens {

    private static final String PARTS_SEPARATOR = "~";
    private static final String DEFAULT_DATA_SEPARATOR = "::";
    private static final int PARTS = 2;

    public static String containing(String dataSeparator, String... data) {
        var hash = UUID.randomUUID().toString();

        var mergedData = String.join(dataSeparator, data);
        var withHash = String.join(PARTS_SEPARATOR, mergedData, hash);

        return Base64Url.asEncoded(withHash);
    }

    public static String containing(String... data) {
        return containing(DEFAULT_DATA_SEPARATOR, data);
    }

    public static Optional<String> decoded(String token) {
        try {
            var decoded = Base64Url.asDecoded(token);
            if (decoded.split(PARTS_SEPARATOR).length == PARTS) {
                return Optional.of(decoded);
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static String extractedData(String decoded) {
        return decoded.split(PARTS_SEPARATOR)[0];
    }

    public static String[] splitAndExtractedData(String decoded) {
        return splitExtractedData(extractedData(decoded));
    }

    public static String[] splitExtractedData(String data) {
        return data.split(DEFAULT_DATA_SEPARATOR);
    }
}
