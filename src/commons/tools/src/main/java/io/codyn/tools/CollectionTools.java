package io.codyn.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionTools {

    public static <T> List<List<T>> toBuckets(Collection<T> collection, int bucketSize) {
        var lists = new ArrayList<List<T>>();

        var currentList = new ArrayList<T>();

        for (var e : collection) {
            currentList.add(e);
            if (currentList.size() == bucketSize) {
                lists.add(currentList);
                currentList = new ArrayList<>();
            }
        }

        if (!currentList.isEmpty()) {
            lists.add(currentList);
        }

        return lists;
    }
}
