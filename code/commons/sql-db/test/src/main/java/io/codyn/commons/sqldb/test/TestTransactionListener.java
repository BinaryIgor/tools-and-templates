package io.codyn.commons.sqldb.test;


import org.jooq.TransactionContext;
import org.jooq.TransactionListener;

//TODO: use and test it!
public class TestTransactionListener implements TransactionListener {


    private Runnable after = () -> {
    };

    public TestTransactionListener after(Runnable after) {
        this.after = after;
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
        after.run();
    }

    @Override
    public void rollbackStart(TransactionContext ctx) {

    }

    @Override
    public void rollbackEnd(TransactionContext ctx) {

    }
}
