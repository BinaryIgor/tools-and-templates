package io.codyn.sqldb.core;

import org.jooq.Field;
import org.jooq.Result;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SqlMapper {

    public static <T> Set<T> nonNullFieldsSet(Result<?> result,
                                              Field<T> field) {
        return result.stream()
                .map(r -> r.get(field))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public static <F, T> Set<T> nonNullFieldsSet(Result<?> result,
                                                 Field<F> field,
                                                 Function<F, T> fieldMapper) {
        return result.stream()
                .map(r -> r.get(field))
                .filter(Objects::nonNull)
                .map(fieldMapper)
                .collect(Collectors.toSet());
    }

    public static <T> List<T> nonNullFieldsList(Result<?> result, Field<T> field) {
        return result.stream()
                .map(r -> r.get(field))
                .filter(Objects::nonNull)
                .toList();
    }
}
