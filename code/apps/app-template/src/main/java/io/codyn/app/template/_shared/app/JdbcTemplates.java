package io.codyn.app.template._shared.app;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public record JdbcTemplates(JdbcTemplate template,
                            NamedParameterJdbcTemplate namedTemplate) {
}
