package org.yi.happy.archive;

import java.io.IOException;

import org.yi.happy.archive.commandLine.Env;
import org.yi.happy.archive.commandLine.UsesArgs;
import org.yi.happy.archive.commandLine.UsesNeed;
import org.yi.happy.archive.commandLine.UsesStore;
import org.yi.happy.archive.file_system.FileSystem;
import org.yi.happy.archive.key.FullKey;
import org.yi.happy.archive.key.FullKeyParse;
import org.yi.happy.archive.tag.RestoreFile;

/**
 * get a file from a file store.
 */
@UsesStore
@UsesNeed
@UsesArgs({ "key", "output" })
public class FileStoreFileGetMain implements MainCommand {
    /**
     * create.
     * 
     * @param fs
     *            the file system to use.
     * @param waitHandler
     *            what to do when it is time to wait for data.
     */
    public FileStoreFileGetMain(FileSystem fs, WaitHandler waitHandler) {
        this.fs = fs;
        this.waitHandler = waitHandler;
    }

    private FileSystem fs;
    private String pendingFile;

    /**
     * get a file from a file store.
     * 
     * @param env
     *            the file store, where to write the pending list, the key to
     *            fetch, the output file name.
     * @throws IOException
     */
    @Override
    public void run(Env env) throws IOException {
        FileBlockStore store = new FileBlockStore(fs, env.getStore());
        pendingFile = env.getNeed();
        FullKey key = FullKeyParse.parseFullKey(env.getArgument(0));
        String path = env.getArgument(1);

        RestoreFile r = new RestoreFile(new SplitReader(key,
                new RetrieveBlockStorage(store)), path, fs);
        r.step();
        while (!r.isDone()) {
            notReady(r);
            r.step();
        }
    }

    private void notReady(RestoreFile reader) throws IOException {
        StringBuilder p = new StringBuilder();
        for (FullKey k : reader.getPending()) {
            p.append(k.toLocatorKey() + "\n");
        }
        fs.save(pendingFile + ".tmp", ByteString.toUtf8(p.toString()));
        fs.rename(pendingFile + ".tmp", pendingFile);

        waitHandler.doWait(progress != reader.getProgress());
        progress = reader.getProgress();
    }

    private int progress;

    private WaitHandler waitHandler;
}
