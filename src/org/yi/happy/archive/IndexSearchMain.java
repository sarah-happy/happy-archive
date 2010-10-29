package org.yi.happy.archive;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.yi.happy.annotate.EntryPoint;
import org.yi.happy.annotate.SmellsMessy;
import org.yi.happy.archive.IndexSearch.SearchResult;
import org.yi.happy.archive.file_system.FileSystem;
import org.yi.happy.archive.file_system.RealFileSystem;
import org.yi.happy.archive.key.LocatorKey;
import org.yi.happy.archive.key.LocatorKeyParse;

/**
 * search indexes for keys.
 */
public class IndexSearchMain {
    private final FileSystem fs;
    private final Writer out;

    /**
     * create with context.
     * 
     * @param fs
     *            the file system.
     * @param out
     *            the output.
     */
    public IndexSearchMain(FileSystem fs, Writer out) {
        this.fs = fs;
        this.out = out;
    }

    /**
     * invoke from the command line.
     * 
     * @param args
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @EntryPoint
    public static void main(String[] args) throws IOException,
            InterruptedException, ExecutionException {
        FileSystem fs = new RealFileSystem();
        Writer out = new OutputStreamWriter(System.out, "utf-8");

        new IndexSearchMain(fs, out).run(args);

        out.flush();
    }

    /**
     * run the index search.
     * 
     * @param args
     *            the command line arguments.
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @SmellsMessy
    public void run(String... args) throws IOException, InterruptedException,
            ExecutionException {
        if (args.length != 2) {
            out.write("use: index key-list\n");
            return;
        }

        Set<LocatorKey> want = loadRequestSet(args[1]);

        IndexSearch search = new IndexSearch(fs, args[0]);
        search.search(want, new IndexSearch.Handler() {
            @Override
            public void gotResult(SearchResult result) {
                try {
                    out.write(result.toString());
                    out.write("\n");
                } catch (IOException e) {
                    throw new IOError(e);
                }
            }

            @Override
            public void gotException(Throwable cause) {
                System.err.println(cause.getMessage());
            }
        });
    }

    private Set<LocatorKey> loadRequestSet(String path) throws IOException {
        Set<LocatorKey> out = new HashSet<LocatorKey>();

        InputStream in0 = fs.openInputStream(path);
        try {
            LineCursor in = new LineCursor(in0);
            while (in.next()) {
                out.add(LocatorKeyParse.parseLocatorKey(in.get()));
            }
        } finally {
            in0.close();
        }

        return out;
    }
}
