package io.codyn.sqldb.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.TransactionListener;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

public class DSLContextFactory {

    public static DSLContext newContext(HikariConfig config, TransactionListener... listeners) {
        return DSL.using(new DefaultConfiguration()
                .set(new HikariDataSource(config))
                .set(SQLDialect.POSTGRES)
                .set(listeners));
    }

    public static DSLContext newContext(String jdbcUrl, String username, String password,
                                        TransactionListener... listeners) {
        var config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(10);
        config.setMaximumPoolSize(10);

        return newContext(config, listeners);
    }
}
