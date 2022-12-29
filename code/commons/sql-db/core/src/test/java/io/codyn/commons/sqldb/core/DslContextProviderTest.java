package io.codyn.commons.sqldb.core;

import org.assertj.core.api.Assertions;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Tag("integration")
public class DslContextProviderTest {

    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:14");

    static {
        POSTGRES.start();
    }

    private DSLContextProvider contextProvider;
    private DSLContext context;


    @BeforeEach
    void setup() {
        context = DSLContextFactory.newContext(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
        contextProvider = new DSLContextProvider(context);
        TestTableRepository.createTable(context);
    }

    @AfterEach
    void tearDown() {
        TestTableRepository.dropTable(context);
    }

    @Test
    void shouldReturnLocalDslContextInTransactionStart() {
        Assertions.assertThat(contextProvider.context()).isEqualTo(context);

        contextProvider.transaction(() -> Assertions.assertThat(contextProvider.context()).isNotEqualTo(context));

        Assertions.assertThat(contextProvider.context()).isEqualTo(context);
    }

    @Test
    void shouldRollbackTransactionWithFailure() {
        var testTable = new TestTable(22, "some-name");

        try {
            contextProvider.transaction(() -> {
                var tCtx = contextProvider.context();

                TestTableRepository.insert(tCtx, testTable);

                Assertions.assertThat(TestTableRepository.exists(tCtx, testTable.id)).isTrue();
                Assertions.assertThat(TestTableRepository.exists(context, testTable.id)).isFalse();

                throw new RuntimeException("Failure");
            });
        } catch (Exception ignored) {

        }

        Assertions.assertThat(TestTableRepository.exists(context, testTable.id)).isFalse();
    }

    @Test
    void shouldReturnTransactionResult() {
        var record = new TestTable(2, "test-table-2");

        var result = contextProvider.transactionResult(() -> {
            TestTableRepository.insert(contextProvider.context(), record);
            return record;
        });

        Assertions.assertThat(result).isEqualTo(record);
    }

    @Test
    void shouldCreateUniqueDslContextPerThreadInTransaction() throws Exception {
        var threads = 5;
        var countDownLatch = new CountDownLatch(threads);

        var tContexts = Collections.newSetFromMap(new ConcurrentHashMap<>());

        IntStream.range(0, threads)
                .forEach(i -> {
                    var t = new Thread(() -> {
                        contextProvider.transaction(() -> {
                            var lContext = contextProvider.context();

                            tContexts.add(lContext);

                            Assertions.assertThat(lContext).isNotEqualTo(context);
                        });

                        countDownLatch.countDown();
                    });
                    t.start();
                });

        if (!countDownLatch.await(1000, TimeUnit.SECONDS)) {
            throw new RuntimeException("Can't finish transactions in one second!");
        }

        Assertions.assertThat(tContexts).hasSize(threads);
    }

    @Test
    void shouldReturnProperContextsAtDifferentPointsInTransaction() {
        Assertions.assertThat(contextProvider.context()).isEqualTo(context);

        contextProvider.transaction(() -> {
            var tContext = contextProvider.context();
            Assertions.assertThat(tContext).isNotEqualTo(context);

            contextProvider.transaction(() -> {
                var nestedContext = contextProvider.context();
                Assertions.assertThat(nestedContext).isEqualTo(tContext);
            });

            Assertions.assertThat(contextProvider.context()).isEqualTo(tContext);
        });

        Assertions.assertThat(contextProvider.context()).isEqualTo(context);
    }

    @Test
    void shouldHandleNestedTransaction() {
        var records = newRecords();
        var firstRecords = records.subList(0, 2);
        var secondRecords = records.subList(2, records.size());
        var createdIds = new ArrayList<Long>();

        contextProvider.transaction(() -> {
            var tContext = contextProvider.context();

            firstRecords.forEach(t -> {
                TestTableRepository.insert(tContext, t);
                createdIds.add(t.id);
            });

            contextProvider.transaction(() -> {
                var nestedContext = contextProvider.context();
                secondRecords.forEach(t -> {
                    TestTableRepository.insert(nestedContext, t);
                    createdIds.add(t.id);
                });
            });
        });

        Assertions.assertThat(createdIds).hasSize(records.size());
        createdIds.forEach(id -> Assertions.assertThat(TestTableRepository.exists(context, id)).isTrue());
    }

    @Test
    void shouldRollbackAllWithUnhandledExceptionInNestedTransaction() {
        var records = newRecords();
        var ids = new ArrayList<Long>();

        Assertions.assertThatThrownBy(() ->
                contextProvider.transaction(() -> {
                    var tContext = contextProvider.context();
                    records.forEach(t -> {
                        TestTableRepository.insert(tContext, t);
                        ids.add(t.id);
                    });

                    ids.forEach(id -> Assertions.assertThat(TestTableRepository.exists(tContext, id)).isTrue());

                    contextProvider.transaction(() -> {
                        var nestedContext = contextProvider.context();
                        records.forEach(t -> {
                            TestTableRepository.insert(nestedContext, t);
                            ids.add(t.id);
                        });
                    });
                })).isInstanceOf(RuntimeException.class);

        Assertions.assertThat(ids).hasSize(records.size());
        ids.forEach(id -> Assertions.assertThat(TestTableRepository.exists(context, id)).isFalse());
    }

    @Test
    void shouldRollbackOnlyInnerTransactionWithHandledExceptionInIt() {
        var records = newRecords();
        var ids = new ArrayList<Long>();

        contextProvider.transaction(() -> {
            var tContext = contextProvider.context();
            records.forEach(t -> {
                TestTableRepository.insert(tContext, t);
                ids.add(t.id);
            });

            ids.forEach(id -> Assertions.assertThat(TestTableRepository.exists(tContext, id)).isTrue());

            try {
                tContext.transaction(ctx -> {
                    var nestedContext = ctx.dsl();
                    records.forEach(t -> {
                        TestTableRepository.insert(nestedContext, t);
                        ids.add(t.id);
                    });
                });
            } catch (Exception ignored) {

            }
        });

        Assertions.assertThat(ids).hasSize(records.size());
        ids.forEach(id -> Assertions.assertThat(TestTableRepository.exists(context, id)).isTrue());
    }

    private List<TestTable> newRecords() {
        return List.of(new TestTable(1, "some-name-1"),
                new TestTable(2, "some-name-2"),
                new TestTable(3, "some-name-3"));
    }

    private record TestTable(long id, String name) {

    }

    private static class TestTableRepository {

        private static final Table<?> TABLE = DSL.table("test_table");
        private static final Field<Long> ID = DSL.field("id", Long.class);


        static void createTable(DSLContext context) {
            context.execute("""
                    CREATE TABLE test_table (
                        id BIGINT PRIMARY KEY,
                        name TEXT NOT NULL
                    );
                    """);
        }

        static void dropTable(DSLContext context) {
            context.dropTable(TABLE).execute();
        }

        static void insert(DSLContext context, TestTable testTable) {
            context.insertInto(TABLE)
                    .values(testTable.id, testTable.name)
                    .execute();
        }

        static boolean exists(DSLContext context, long id) {
            return context.fetchExists(TABLE, ID.eq(id));
        }
    }
}
