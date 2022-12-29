package io.codyn.commons.sqldb.test;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class TestDbTransactionTest extends DbIntegrationTest {

    private static final Runnable NO_OP_TRANSACTION = () -> {
    };

    @Test
    void shouldBeCalledBeforeAndAfterTransactionWithTransaction() {
        var beforeCalled = new AtomicBoolean(false);
        var afterCalled = new AtomicBoolean(false);

        transactionTest()
                .before(() -> beforeCalled.set(true))
                .after(() -> afterCalled.set(true))
                .execute(() -> contextProvider.transaction(NO_OP_TRANSACTION));

        Assertions.assertThat(beforeCalled.get()).isTrue();
        Assertions.assertThat(afterCalled.get()).isTrue();
    }

    @Test
    void shouldNotBeCalledBeforeAndAfterTransactionWithoutTransaction() {
        var beforeCalled = new AtomicBoolean(false);
        var afterCalled = new AtomicBoolean(false);

        transactionTest()
                .before(() -> beforeCalled.set(true))
                .after(() -> afterCalled.set(true));

        Assertions.assertThat(beforeCalled.get()).isFalse();
        Assertions.assertThat(afterCalled.get()).isFalse();
    }


    @Test
    void shouldThrowMeaningfulExceptionWithBeforeFailure() {
        var exceptionMessage = "Before Exception!";

        Assertions.assertThatThrownBy(() ->
                        transactionTest()
                                .before(() -> {
                                    throw new RuntimeException(exceptionMessage);
                                })
                                .after(() -> {
                                })
                                .execute(() -> contextProvider.transaction(NO_OP_TRANSACTION)))
                .hasMessage(exceptionMessage);
    }

    @Test
    void shouldThrowMeaningfulExceptionWithAfterFailure() {
        var exceptionMessage = "After Exception!";

        Assertions.assertThatThrownBy(() ->
                        transactionTest()
                                .before(() -> {
                                })
                                .after(() -> {
                                    throw new RuntimeException(exceptionMessage);
                                })
                                .execute(() -> contextProvider.transaction(NO_OP_TRANSACTION)))
                .hasMessage(exceptionMessage);
    }

    @Test
    void shouldThrowMeaningfulExceptionWithTransactionFailure() {
        var exceptionMessage = "Transaction Exception!";

        Assertions.assertThatThrownBy(() ->
                        transactionTest()
                                .before(() -> {
                                })
                                .after(() -> {
                                })
                                .execute(() -> contextProvider.transaction(() -> {
                                    throw new RuntimeException(exceptionMessage);
                                })))
                .hasMessage(exceptionMessage);
    }
}
