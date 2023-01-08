package io.codyn.app.template._shared.app;

import java.util.Base64;

public class StringBytesMapper {

    public static byte[] bytesFromString(String string) {
        if (string == null) {
            return null;
        }
        return Base64.getDecoder().decode(string);
    }
}
