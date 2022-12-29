package io.codyn.commons.sqldb.test;

import io.codyn.commons.sqldb.core.DSLContextFactory;
import io.codyn.commons.sqldb.core.DSLContextProvider;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

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
    protected DSLContextProvider contextProvider;

    @BeforeEach
    void setupContext() {
        context = DSLContextFactory.newContext(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword(),
                transactionListener);
        contextProvider = new DSLContextProvider(context);
        setup();
    }

    protected void setup() {

    }

    @AfterEach
    protected void tearDown() {
        POSTGRES.clearDb();
    }

    protected TestDbTransaction transactionTest() {
        return new TestDbTransaction(transactionListener);
    }
}
