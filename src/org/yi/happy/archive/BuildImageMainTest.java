package org.yi.happy.archive;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.yi.happy.archive.commandLine.Env;
import org.yi.happy.archive.commandLine.EnvBuilder;
import org.yi.happy.archive.file_system.FakeFileSystem;
import org.yi.happy.archive.file_system.FileSystem;
import org.yi.happy.archive.key.ContentLocatorKey;
import org.yi.happy.archive.key.HashValue;
import org.yi.happy.archive.test_data.TestData;

/**
 * Tests for {@link BuildImageMain}.
 */
public class BuildImageMainTest {
    /**
     * A sample good run.
     * 
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        FileSystem fs = new FakeFileSystem();
        BlockStore store = new StorageMemory();
        store.put(TestData.KEY_CONTENT.getEncodedBlock());
        CapturePrintStream out = CapturePrintStream.create();
        fs.save("outstanding",
                ByteString.toUtf8(TestData.KEY_CONTENT.getLocatorKey()
                        .toString() + "\n"));
        fs.mkdir("output");

        Env env = new EnvBuilder().withStore("store")
                .addArgument("outstanding").addArgument("output")
                .addArgument("4700").create();
        new BuildImageMain(store, fs, out, null, env).run();

        assertArrayEquals(TestData.KEY_CONTENT.getBytes(),
                fs.load("output/00000000.dat"));
        assertEquals("1\t1\n", out.toString());
    }

    /**
     * Another sample good run.
     * 
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {
        FileSystem fs = new FakeFileSystem();
        BlockStore store = new StorageMemory();
        store.put(TestData.KEY_CONTENT.getEncodedBlock());
        store.put(TestData.KEY_CONTENT_1.getEncodedBlock());
        CapturePrintStream out = CapturePrintStream.create();
        fs.save("outstanding",
                ByteString.toUtf8(TestData.KEY_CONTENT.getLocatorKey() + "\n"
                        + TestData.KEY_CONTENT_1.getLocatorKey() + "\n"));
        fs.mkdir("output");

        Env env = new EnvBuilder().withStore("store")
                .addArgument("outstanding").addArgument("output")
                .addArgument("4700").create();
        new BuildImageMain(store, fs, out, null, env).run();

        assertArrayEquals(TestData.KEY_CONTENT.getBytes(),
                fs.load("output/00000000.dat"));
        assertArrayEquals(TestData.KEY_CONTENT_1.getBytes(),
                fs.load("output/00000001.dat"));
        assertEquals("2\t1\n", out.toString());
    }

    /**
     * Broken block in store
     * 
     * @throws IOException
     */
    @Test
    public void testBrokenBlockInStore() throws IOException {
        FileSystem fs = new FakeFileSystem();
        StorageMemory store = new StorageMemory();
        store.put(TestData.KEY_CONTENT.getEncodedBlock());
        store.put(TestData.KEY_CONTENT_1.getEncodedBlock());

        /* put a broken block in the store */
        store.putBroken(new ContentLocatorKey(new HashValue(0x00, 0x00, 0x00,
                0x00)));

        CapturePrintStream out = CapturePrintStream.create();
        fs.save("outstanding",
                ByteString.toUtf8(TestData.KEY_CONTENT.getLocatorKey() + "\n"
                        + TestData.KEY_CONTENT_1.getLocatorKey() + "\n"
                        + "content-hash:00000000\n"));
        fs.mkdir("output");

        Env env = new EnvBuilder().withStore("store")
                .addArgument("outstanding").addArgument("output")
                .addArgument("4700").create();
        new BuildImageMain(store, fs, out, new NullPrintStream(), env).run();

        assertArrayEquals(TestData.KEY_CONTENT.getBytes(),
                fs.load("output/00000000.dat"));
        assertArrayEquals(TestData.KEY_CONTENT_1.getBytes(),
                fs.load("output/00000001.dat"));
        assertEquals("2\t1\n", out.toString());
    }

}
