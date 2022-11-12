package com.igor101.system.monitor.logs.core.model;

import java.util.List;

public record LogMapping(List<String> warningKeywords,
                         List<String> errorKeywords,
                         List<String> messagesToSwallow) {

    public LogMapping {
        if (warningKeywords == null || warningKeywords.isEmpty()) {
            throw new RuntimeException("Empty or null warning keywords");
        }
        if (errorKeywords == null || errorKeywords.isEmpty()) {
            throw new RuntimeException("Empty or null error keywords");
        }
        if (messagesToSwallow == null) {
            messagesToSwallow = List.of();
        }
    }
}
