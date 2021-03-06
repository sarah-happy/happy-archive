package org.yi.happy.archive.block;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.yi.happy.archive.test_data.TestData;

/**
 * Tests for {@link MapBlock}.
 */
public class MapBlockTest {
    /**
     * build the test data.
     * 
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        MapBlock block = new MapBlock(Arrays.asList(new MapBlock.Entry(
                TestData.KEY_CONTENT_1.getFullKey(), 0), new MapBlock.Entry(
                TestData.KEY_CONTENT_2.getFullKey(), 5)));

        assertEquals(TestData.CLEAR_CONTENT_MAP.getBlock(), block);
    }

    /**
     * build the test data.
     * 
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {
        MapBlock block = new MapBlock(Arrays.asList(new MapBlock.Entry(
                TestData.KEY_CONTENT_1.getFullKey(), 0), new MapBlock.Entry(
                TestData.KEY_CONTENT_2.getFullKey(), 10)));

        assertArrayEquals(TestData.CLEAR_CONTENT_MAP_PAD.getBytes(), block
                .asBytes());
    }
}
