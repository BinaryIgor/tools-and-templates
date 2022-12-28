package io.codyn.commons.tools;

import java.util.function.Supplier;

public interface Transactions {

    void execute(Runnable transaction);


     <T> T executeAndReturn(Supplier<T> transaction);
}
