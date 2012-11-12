package org.yi.happy.archive;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.yi.happy.annotate.NeedFailureTest;
import org.yi.happy.archive.file_system.FakeFileSystem;
import org.yi.happy.archive.test_data.TestData;

/**
 * Tests for {@link FileStoreBlockPutMain}.
 */
@NeedFailureTest
public class FileStoreBlockPutMainTest {
    /**
     * A normal good usage test.
     * 
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        FakeFileSystem fs = new FakeFileSystem();
        StorageMemory store = new StorageMemory();
        fs.save("block.dat", TestData.KEY_CONTENT.getBytes());

        List<String> args = Arrays.asList("block.dat");
        new FileStoreBlockPutMain(store, fs, args).run();

        assertTrue(store.contains(TestData.KEY_CONTENT.getLocatorKey()));
    }
}
