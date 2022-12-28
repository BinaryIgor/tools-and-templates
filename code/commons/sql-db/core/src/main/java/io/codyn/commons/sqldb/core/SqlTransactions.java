package io.codyn.commons.sqldb.core;

import io.codyn.commons.tools.Transactions;
import org.jooq.DSLContext;

import java.util.function.Supplier;

//TODO: impl, tests
public class SqlTransactions implements Transactions {

    private static final ThreadLocal<Boolean> CURRENT_TRANSACTION = new ThreadLocal<>();
    private final DSLContext context;
    private final ThreadLocal<DSLContext> transactionalContexts = new ThreadLocal<>();

    public SqlTransactions(DSLContext context) {
        this.context = context;
    }

    @Override
    public void execute(Runnable transaction) {
        executeAndReturn(() -> {
            transaction.run();
            return null;
        });
    }

    @Override
    public <T> T executeAndReturn(Supplier<T> transaction) {
        var tContext = transactionalContexts.get();
        if (tContext == null) {
            return context.transactionResult(t -> {
                transactionalContexts.set(t.dsl());
                try {
                    return transaction.get();
                } finally {
                    transactionalContexts.remove();
                }
            });
        }

        return transaction.get();
    }

}
