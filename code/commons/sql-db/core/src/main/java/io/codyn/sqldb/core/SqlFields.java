package io.codyn.sqldb.core;

import org.jooq.Field;
import org.jooq.impl.DSL;

public class SqlFields {

    public static <T> Field<T> excluded(Field<T> field) {
        return DSL.field("excluded." + field.getName(), field.getType());
    }
}
