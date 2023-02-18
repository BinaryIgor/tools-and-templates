package io.codyn.app.template._common.infra;

import io.codyn.app.template._common.core.exception.OptimisticLockException;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import java.util.UUID;

public class SqlOptimisticLock {

    public static final Field<UUID> DEFAULT_ID_FIELD = DSL.field("id", UUID.class);
    public static final Field<Long> DEFAULT_VERSION_FIELD = DSL.field("version", long.class);


    public static <R extends Record & UpdatableRecord<R>> void upsert(DSLContext context,
                                                                      TableImpl<R> table,
                                                                      UpdatableRecord<R> record) {
        upsert(context, table, record, DEFAULT_ID_FIELD, DEFAULT_VERSION_FIELD);
    }

    public static <ID, R extends Record & UpdatableRecord<R>> void upsert(DSLContext context,
                                                                      TableImpl<R> table,
                                                                      UpdatableRecord<R> record,
                                                                      Field<ID> idField,
                                                                      Field<Long> versionField) {
        var nextVersion = record.get(versionField);

        if (nextVersion == 1) {
            context.insertInto(table)
                    .set(record)
                    .execute();
        } else {
            var currentVersion = nextVersion - 1;

            var updated = context.update(table)
                    .set(record)
                    .where(idField.eq(record.get(idField))
                            .and(versionField.eq(currentVersion)))
                    .execute();

            if (updated == 0) {
                throw new OptimisticLockException(table.getName());
            }
        }
    }
}
