package org.max.revolut;

import java.util.function.Supplier;

public final class ArrayUtils {

    private ArrayUtils() {
        throw new IllegalStateException("Can't instantiate utility-only class");
    }

    public static <T> T checkNotNull(T obj, Supplier<String> errorMsg) {
        if (obj == null) {
            throw new IllegalArgumentException(errorMsg.get());
        }
        return obj;
    }

    public static void checkInRange(int index, int[] arr, Supplier<String> errorMsgSup) {
        if (index < 0 || index >= arr.length) {
            throw new IllegalArgumentException(errorMsgSup.get());
        }
    }

    private static void checkIndexBoundary(int index, int from, int to, Supplier<String> errorMsg) {
        if (index < from || index > to) {
            throw new IndexOutOfBoundsException(errorMsg.get());
        }
    }

    public static void swap(int[] arr, int from, int to) {
        checkNotNull(arr, () -> "null 'arr' detected");
        checkInRange(from, arr, () -> String.format("from = %s, but should be in range [%s, %s]", from, 0, arr.length - 1));
        checkInRange(to, arr, () -> String.format("to = %s, but should be in range [%s, %s]", to, 0, arr.length - 1));

        int temp = arr[from];

        arr[from] = arr[to];
        arr[to] = temp;
    }

}
