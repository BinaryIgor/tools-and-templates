package io.codyn.commons.sqldb.core;

import io.codyn.commons.tools.Transactions;
import org.jooq.DSLContext;

import java.util.function.Supplier;

//TODO: impl, tests
public class SqlTransactions implements Transactions {

    private final DSLContext context;

    public SqlTransactions(DSLContext context) {
        this.context = context;
    }

    @Override
    public void execute(Runnable transaction) {

    }

    @Override
    public <T> T executeAndReturn(Supplier<T> transaction) {
        return null;
    }
}
