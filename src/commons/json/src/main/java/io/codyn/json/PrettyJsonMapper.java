package io.codyn.json;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PrettyJsonMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        var printer = new DefaultPrettyPrinter();

        var newLineIndenter = new DefaultIndenter("  ", "\n");

        printer.indentArraysWith(newLineIndenter);
        printer.indentObjectsWith(newLineIndenter);

        OBJECT_MAPPER.setDefaultPrettyPrinter(printer);
    }

    public static String json(Object object) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
