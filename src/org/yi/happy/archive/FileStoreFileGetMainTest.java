package org.yi.happy.archive;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.yi.happy.archive.test_data.TestData;

/**
 * Tests for {@link FileStoreFileGetMain}.
 */
public class FileStoreFileGetMainTest {
    /**
     * an expected good run.
     * 
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        /*
         * NOTE this is not strictly speaking a unit test since there are two
         * layers of objects in use to exercise the functionality.
         */

        final FragmentSaveMemory target = new FragmentSaveMemory();
        final MapClearBlockSource store = new MapClearBlockSource();
        final NeedCapture needHandler = new NeedCapture();

        WaitHandler waitHandler = new WaitHandler() {
            @Override
            public void doWait(boolean progress) throws IOException {
                state.doWait(progress);
            }

            private WaitHandler state = new WaitHandler() {
                @Override
                public void doWait(boolean progress) throws IOException {
                    assertFalse(progress);

                    /*
                     * check the request list
                     */
                    assertEquals(Arrays.asList(TestData.KEY_CONTENT_MAP
                            .getLocatorKey()), needHandler.getKeys());

                    /*
                     * add the map block to the store
                     */
                    store.put(TestData.KEY_CONTENT_MAP);

                    state = state2;
                }
            };

            private final WaitHandler state2 = new WaitHandler() {
                @Override
                public void doWait(boolean progress) throws IOException {
                    assertTrue(progress);

                    /*
                     * check the request list
                     */
                    assertEquals(Arrays.asList(
                            TestData.KEY_CONTENT_1.getLocatorKey(),
                            TestData.KEY_CONTENT_2.getLocatorKey()),
                            needHandler.getKeys());

                    /*
                     * add the second part
                     */
                    store.put(TestData.KEY_CONTENT_2);

                    state = state3;
                }
            };

            private final WaitHandler state3 = new WaitHandler() {
                @Override
                public void doWait(boolean progress) throws IOException {
                    assertTrue(progress);

                    /*
                     * check the request list
                     */
                    assertEquals(Arrays.asList(TestData.KEY_CONTENT_1
                            .getLocatorKey()), needHandler.getKeys());

                    /*
                     * add the first part
                     */
                    store.put(TestData.KEY_CONTENT_1);

                    state = state4;
                }
            };

            private final WaitHandler state4 = new WaitHandler() {
                @Override
                public void doWait(boolean progress) {
                    fail();
                }
            };

        };

        List<String> args = Arrays.asList(TestData.KEY_CONTENT_MAP.getFullKey()
                .toString(), "out");
        new FileStoreFileGetMain(store, target, waitHandler, needHandler, args)
                .run();

        assertArrayEquals(ByteString.toUtf8("0123456789"), target.get("out"));
    }
}
