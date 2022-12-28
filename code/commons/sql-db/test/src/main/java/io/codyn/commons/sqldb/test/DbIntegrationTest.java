package io.codyn.commons.sqldb.test;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import java.sql.DriverManager;
import java.util.TimeZone;

@Tag("integration")
public abstract class DbIntegrationTest {

    protected static CustomPostgreSQLContainer POSTGRES = CustomPostgreSQLContainer.instance();

    static {
        //Prevent strange behavior during daylight saving time
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    protected final TestTransactionListener transactionListener = new TestTransactionListener();
    protected DSLContext context;

    @BeforeEach
    void setupContext() {
        context = dbContext();
        setup();
    }

    private DSLContext dbContext() {
        try {
            var connection = DriverManager.getConnection(POSTGRES.getJdbcUrl(),
                    POSTGRES.getUsername(),
                    POSTGRES.getPassword());

            return DSL.using(new DefaultConfiguration()
                    .set(connection)
                    .set(SQLDialect.POSTGRES)
                    .set(transactionListener));
        } catch (Exception e) {
            throw new RuntimeException("Can't connect!", e);
        }
    }

    protected void setup() {

    }

    @AfterEach
    protected void tearDown() {
        POSTGRES.clearDb();
    }
}
