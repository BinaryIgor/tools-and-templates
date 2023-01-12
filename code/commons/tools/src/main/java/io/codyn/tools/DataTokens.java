package io.codyn.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

//TODO: tests
public class DataTokens {

    private static final String PARTS_SEPARATOR = "~";
    private static final String DATA_ELEMENT_START = "{{";
    private static final String DATA_ELEMENT_END = "}}";
    private static final Pattern DATA_ELEMENT_PATTERN = Pattern.compile("\\{\\{(.+?)}}");
    private static final int PARTS = 2;

    public static String containing(String... data) {
        if (data.length == 0) {
            throw new RuntimeException("Data can't be empty");
        }

        var hash = UUID.randomUUID().toString();
        var withHash = String.join(PARTS_SEPARATOR, mergedData(), hash);

        return Base64Url.asEncoded(withHash);
    }

    private static String mergedData(String... data) {
        var builder = new StringBuilder();
        for (var e : data) {
            builder.append(DATA_ELEMENT_START)
                    .append(e)
                    .append(DATA_ELEMENT_END);
        }
        return builder.toString();
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

    public static List<String> splitAndExtractedData(String decoded) {
        return splitExtractedData(extractedData(decoded));
    }

    public static List<String> splitExtractedData(String data) {
        var dataElements = new ArrayList<String>();

        var matcher = DATA_ELEMENT_PATTERN.matcher(data);
        while (matcher.find()) {
            var el = matcher.group(1);
            dataElements.add(el);
        }

        return dataElements;
    }
}
