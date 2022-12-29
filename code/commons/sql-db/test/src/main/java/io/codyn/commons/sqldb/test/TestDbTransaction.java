package io.codyn.commons.sqldb.test;

import org.junit.jupiter.api.Assertions;

public class TestDbTransaction {

    private final TestTransactionListener listener;
    private Runnable before = () -> {
    };
    private boolean afterCheckCalled;


    public TestDbTransaction(TestTransactionListener listener) {
        this.listener = listener;
    }

    public TestDbTransaction before(Runnable beforeCheck) {
        this.before = beforeCheck;
        return this;
    }

    public TestDbTransaction after(Runnable afterCheck) {
        listener.afterCommit(() -> {
            afterCheckCalled = true;
            afterCheck.run();
        });

        listener.afterRollback(() -> afterCheckCalled = true);

        afterCheckCalled = false;
        return this;
    }

    public void execute(Runnable transaction) {
        before.run();
        try {
            transaction.run();
        } finally {
            Assertions.assertTrue(afterCheckCalled);
        }
    }
}
