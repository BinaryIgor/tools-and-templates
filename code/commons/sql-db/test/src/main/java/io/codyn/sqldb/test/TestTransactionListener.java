package io.codyn.sqldb.test;

import org.jooq.TransactionContext;
import org.jooq.TransactionListener;

public class TestTransactionListener implements TransactionListener {


    private Runnable afterCommit = () -> {
    };

    private Runnable afterRollback = () -> {
    };

    public TestTransactionListener afterCommit(Runnable after) {
        afterCommit = after;
        return this;
    }

    public TestTransactionListener afterRollback(Runnable after) {
        afterRollback = after;
        return this;
    }

    @Override
    public void beginStart(TransactionContext ctx) {
    }

    @Override
    public void beginEnd(TransactionContext ctx) {

    }

    @Override
    public void commitStart(TransactionContext ctx) {

    }

    @Override
    public void commitEnd(TransactionContext ctx) {
        afterCommit.run();
    }

    @Override
    public void rollbackStart(TransactionContext ctx) {

    }

    @Override
    public void rollbackEnd(TransactionContext ctx) {
        afterRollback.run();
    }
}
