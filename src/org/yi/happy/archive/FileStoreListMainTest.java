package org.yi.happy.archive;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;
import org.yi.happy.archive.commandLine.Env;
import org.yi.happy.archive.commandLine.EnvBuilder;
import org.yi.happy.archive.test_data.TestData;

/**
 * Tests for {@link FileStoreListMain}.
 */
public class FileStoreListMainTest {
    /**
     * A good run.
     * 
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        BlockStore store = new StorageMemory();
        store.put(TestData.KEY_CONTENT.getEncodedBlock());
        store.put(TestData.KEY_CONTENT_1.getEncodedBlock());
        store.put(TestData.KEY_CONTENT_2.getEncodedBlock());

        ByteArrayOutputStream out0 = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(out0, true);

        Env env = new EnvBuilder().withStore("store").create();
        new FileStoreListMain(store, null, out, null).run();

        String want = TestData.KEY_CONTENT.getLocatorKey() + "\n"
                + TestData.KEY_CONTENT_2.getLocatorKey() + "\n"
                + TestData.KEY_CONTENT_1.getLocatorKey() + "\n";

        assertEquals(want, out0.toString("UTF-8"));
    }

}
