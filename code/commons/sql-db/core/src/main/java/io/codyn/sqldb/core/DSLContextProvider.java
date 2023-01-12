package io.codyn.sqldb.core;

import org.jooq.DSLContext;

import java.util.function.Supplier;

public class DSLContextProvider {

    private final ThreadLocal<DSLContext> transactionalContexts = new ThreadLocal<>();
    private final DSLContext context;

    public DSLContextProvider(DSLContext context) {
        this.context = context;
    }

    public void transaction(Runnable transaction) {
        transactionResult(() -> {
            transaction.run();
            return true;
        });
    }

    public <T> T transactionResult(Supplier<T> transaction) {
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

    public DSLContext context() {
        var tContext = transactionalContexts.get();
        return tContext == null ? context : tContext;
    }
}
