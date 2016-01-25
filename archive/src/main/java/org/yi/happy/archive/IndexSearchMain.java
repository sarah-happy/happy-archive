package org.yi.happy.archive;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.yi.happy.archive.commandLine.UsesIndexStore;
import org.yi.happy.archive.commandLine.UsesInput;
import org.yi.happy.archive.commandLine.UsesOutput;
import org.yi.happy.archive.index.IndexEntry;
import org.yi.happy.archive.index.IndexSearch;
import org.yi.happy.archive.key.LocatorKey;
import org.yi.happy.archive.key.LocatorKeyParse;

/**
 * search indexes for keys.
 */
@UsesIndexStore
@UsesInput("key-list")
@UsesOutput("result")
public class IndexSearchMain implements MainCommand {
    private final PrintStream out;
    private final IndexSearch indexSearch;
    private final PrintStream err;
    private final InputStream in;

    /**
     * create with context.
     * 
     * @param in
     *            the input stream.
     * @param out
     *            the output stream.
     * @param err
     *            the error stream.
     * @param indexSearch
     *            the index search interface.
     */
    public IndexSearchMain(InputStream in, PrintStream out, PrintStream err,
            IndexSearch indexSearch) {
        this.in = in;
        this.out = out;
        this.err = err;
        this.indexSearch = indexSearch;
    }

    /**
     * run the index search.
     * 
     * @param env
     *            the command line arguments.
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public void run() throws IOException, InterruptedException,
            ExecutionException {
        Set<LocatorKey> keys = loadKeyList();

        indexSearch.search(keys, new IndexSearch.Handler() {
            @Override
            public void gotResult(String volumeSet, String volumeName,
                    IndexEntry result) {
                out.println(volumeSet + "\t" + volumeName + "\t"
                        + result.getName() + "\t" + result.getKey());
            }

            @Override
            public void gotException(String volumeSet, String volumeName,
                    Throwable cause) {
                cause.printStackTrace(err);
            }
        });

        out.flush();
    }

    private Set<LocatorKey> loadKeyList() throws IOException {
        Set<LocatorKey> keys = new HashSet<LocatorKey>();
        for (String line : new LineIterator(in)) {
            keys.add(LocatorKeyParse.parseLocatorKey(line));
        }
        return keys;
    }
}