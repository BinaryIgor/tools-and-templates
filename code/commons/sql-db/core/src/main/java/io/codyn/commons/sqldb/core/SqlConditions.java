package io.codyn.commons.sqldb.core;

import org.jooq.Condition;
import org.jooq.impl.DSL;

import java.util.Collection;
import java.util.function.Function;

public class SqlConditions {

    public static <T> Condition ors(Collection<T> data, Function<T, Condition> conditionFactory) {
        return DSL.or(data.stream().map(conditionFactory).toList());
    }
}
