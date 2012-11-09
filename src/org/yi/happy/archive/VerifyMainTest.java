package org.yi.happy.archive;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Test;
import org.yi.happy.archive.commandLine.Env;
import org.yi.happy.archive.commandLine.EnvBuilder;
import org.yi.happy.archive.file_system.FakeFileSystem;
import org.yi.happy.archive.test_data.TestData;

/**
 * tests for {@link VerifyMain}.
 */
public class VerifyMainTest {
    /**
     * verify a good file.
     * 
     * @throws Exception
     */
    @Test
    public void testOk() throws Exception {
        StringWriter out = new StringWriter();
        FakeFileSystem fs = new FakeFileSystem();
        fs.save(TestData.KEY_CONTENT.getFileName(), TestData.KEY_CONTENT
                .getBytes());
        VerifyMain app = new VerifyMain(fs, out);

        Env env = new EnvBuilder().addArgument(
                TestData.KEY_CONTENT.getFileName()).create();
        app.run(env);

        assertEquals("ok " + TestData.KEY_CONTENT.getLocatorKey() + " "
                + TestData.KEY_CONTENT.getFileName() + "\n", out.toString());
    }

    /**
     * verify a missing file.
     * 
     * @throws Exception
     */
    @Test
    public void testMissing() throws Exception {
        StringWriter out = new StringWriter();
        FakeFileSystem fs = new FakeFileSystem();
        VerifyMain app = new VerifyMain(fs, out);

        Env env = new EnvBuilder().addArgument("file.dat").create();
        app.run(env);

        assertEquals("fail file.dat\n", out.toString());
    }
}
