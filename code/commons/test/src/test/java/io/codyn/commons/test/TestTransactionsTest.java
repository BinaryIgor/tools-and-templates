package io.codyn.commons.test;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class TestTransactionsTest {

    private static final Runnable NO_OP_TRANSACTION = () -> {
    };
    private TestTransactions transactions;

    @BeforeEach
    void setup() {
        transactions = new TestTransactions();
    }

    @Test
    void shouldCallBeforeAndAfterChecksWithTransaction() {
        var beforeCalled = new AtomicBoolean(false);
        var afterCalled = new AtomicBoolean(false);

        transactions.test()
                .before(() -> beforeCalled.set(true))
                .after(() -> afterCalled.set(true))
                .execute(() -> transactions.execute(NO_OP_TRANSACTION));

        Assertions.assertThat(beforeCalled.get()).isTrue();
        Assertions.assertThat(afterCalled.get()).isTrue();
    }

    @Test
    void shouldNotCallBeforeAndAfterChecksWithoutTransaction() {
        var beforeCalled = new AtomicBoolean(false);
        var afterCalled = new AtomicBoolean(false);

        transactions.test()
                .before(() -> beforeCalled.set(true))
                .after(() -> afterCalled.set(true));

        Assertions.assertThat(beforeCalled.get()).isFalse();
        Assertions.assertThat(afterCalled.get()).isFalse();
    }

    @Test
    void shouldThrowMeaningfulExceptionWithBeforeFailure() {
        var exceptionMessage = "Before Exception!";

        Assertions.assertThatThrownBy(() ->
                        transactions.test()
                                .before(() -> {
                                    throw new RuntimeException(exceptionMessage);
                                })
                                .after(() -> {
                                })
                                .execute(() -> transactions.execute(NO_OP_TRANSACTION)))
                .hasMessage(exceptionMessage);
    }

    @Test
    void shouldThrowMeaningfulExceptionWithAfterFailure() {
        var exceptionMessage = "After Exception!";

        Assertions.assertThatThrownBy(() ->
                        transactions.test()
                                .before(() -> {
                                })
                                .after(() -> {
                                    throw new RuntimeException(exceptionMessage);
                                })
                                .execute(() -> transactions.execute(NO_OP_TRANSACTION)))
                .hasMessage(exceptionMessage);
    }

    @Test
    void shouldThrowMeaningfulExceptionWithTransactionFailure() {
        var exceptionMessage = "Transaction Exception!";

        Assertions.assertThatThrownBy(() ->
                        transactions.test()
                                .before(() -> {
                                })
                                .after(() -> {
                                })
                                .execute(() -> transactions.execute(() -> {
                                    throw new RuntimeException(exceptionMessage);
                                })))
                .hasMessage(exceptionMessage);
    }
}
