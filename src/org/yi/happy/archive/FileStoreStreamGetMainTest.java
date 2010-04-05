package org.yi.happy.archive;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.yi.happy.annotate.SmellsMessy;
import org.yi.happy.archive.file_system.FakeFileSystem;
import org.yi.happy.archive.file_system.FileSystem;
import org.yi.happy.archive.test_data.TestData;

/**
 * Tests for {@link FileStoreStreamGetMain}.
 */
public class FileStoreStreamGetMainTest {
    /**
     * an expected good run.
     * 
     * @throws IOException
     */
    @Test
    @SmellsMessy
    public void test1() throws IOException {
        /*
         * NOTE this is not strictly speaking a unit test since there are two
         * layers of objects in use to exercise the functionality.
         */

        final FileSystem fs = new FakeFileSystem();
        final FileBlockStore store = new FileBlockStore(fs, "store");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        WaitHandler waitHandler = new WaitHandler() {
            public void doWait(boolean progress) throws IOException {
                state.doWait(progress);
            }

            private WaitHandler state = new WaitHandler() {
                public void doWait(boolean progress) throws IOException {
                    assertFalse(progress);

                    /*
                     * check the request list
                     */
                    String want = TestData.KEY_CONTENT_MAP.getLocatorKey()
                            + "\n";
                    assertArrayEquals(ByteString.toUtf8(want), fs
                            .load("request"));

                    /*
                     * add the map block to the store
                     */
                    store.put(TestData.KEY_CONTENT_MAP.getEncodedBlock());

                    state = state2;
                }
            };

            private final WaitHandler state2 = new WaitHandler() {
                public void doWait(boolean progress) throws IOException {
                    assertTrue(progress);

                    /*
                     * check the request list
                     */
                    String want = TestData.KEY_CONTENT_1.getLocatorKey() + "\n"
                            + TestData.KEY_CONTENT_2.getLocatorKey() + "\n";
                    assertArrayEquals(ByteString.toUtf8(want), fs
                            .load("request"));

                    /*
                     * add the second part
                     */
                    store.put(TestData.KEY_CONTENT_2.getEncodedBlock());

                    state = state3;
                }
            };

            private final WaitHandler state3 = new WaitHandler() {
                public void doWait(boolean progress) throws IOException {
                    assertFalse(progress);

                    /*
                     * check the request list
                     */
                    String want = TestData.KEY_CONTENT_1.getLocatorKey() + "\n"
                            + TestData.KEY_CONTENT_2.getLocatorKey() + "\n";
                    assertArrayEquals(ByteString.toUtf8(want), fs
                            .load("request"));

                    /*
                     * add the first part
                     */
                    store.put(TestData.KEY_CONTENT_1.getEncodedBlock());

                    state = state4;
                }
            };

            private final WaitHandler state4 = new WaitHandler() {
                public void doWait(boolean progress) {
                    fail();
                }
            };

        };

        new FileStoreStreamGetMain(fs, out, waitHandler).run("store",
                "request", TestData.KEY_CONTENT_MAP.getFullKey().toString());

        assertArrayEquals(ByteString.toUtf8("0123456789"), out.toByteArray());
    }
}
