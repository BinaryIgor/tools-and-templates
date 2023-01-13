package io.codyn.tools;

import io.codyn.json.JsonMapper;

import java.util.Optional;
import java.util.UUID;

public class DataTokens {

    private static final String PARTS_SEPARATOR = "~";
    private static final int PARTS = 2;

    public static String containing(Object data) {
        var hash = UUID.randomUUID().toString();

        String inTokenData;
        if (data.getClass().isAssignableFrom(String.class)) {
            inTokenData = data.toString();
        } else {
            inTokenData = JsonMapper.json(data);
        }

        var withHash = String.join(PARTS_SEPARATOR, inTokenData, hash);

        return Base64Url.asEncoded(withHash);
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

    public static <T> T extractedData(String decoded, Class<T> dataType) {
        var data = extractedData(decoded);
        return JsonMapper.object(data, dataType);
    }
}
