package org.max.revolut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class ArrayUtilsTest {

    @Test
    public void swapNormalCase() {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        ArrayUtils.swap(arr, 1, 4);

        assertEquals(5, arr[1]);
        assertEquals(2, arr[4]);
    }

    @Test
    public void swapOnBoundary() {
        int[] arr = {1, 2, 3, 4, 5};

        ArrayUtils.swap(arr, 0, arr.length-1);

        assertEquals(5, arr[0]);
        assertEquals(1, arr[arr.length-1]);
    }

    @Test
    public void swapWithNegativeIndexShouldFail() {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> ArrayUtils.swap(arr, -3, 4));

        assertEquals("from = -3, but should be in range [0, 9]", ex.getMessage());
    }

    @Test
    public void swapWithBigIndexShouldFail() {
        int[] arr = {1, 2, 3};

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> ArrayUtils.swap(arr, 0, 10));

        assertEquals("to = 10, but should be in range [0, 2]", ex.getMessage());
    }

    @Test
    public void swapWithNullArrayShouldFail() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> ArrayUtils.swap(null, 0, 10));

        assertEquals("null 'arr' detected", ex.getMessage());
    }

}
