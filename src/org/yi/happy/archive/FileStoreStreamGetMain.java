package org.yi.happy.archive;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;

import org.yi.happy.annotate.EntryPoint;
import org.yi.happy.annotate.SmellsMessy;
import org.yi.happy.archive.file_system.FileSystem;
import org.yi.happy.archive.file_system.RealFileSystem;
import org.yi.happy.archive.key.FullKey;
import org.yi.happy.archive.key.KeyParse;
import org.yi.happy.archive.key.KeyUtil;

/**
 * Fetch a stream, the blocks may not all available in the file store, so the
 * ones that are needed are put in a list, and the process continues to be
 * retried until all the needed blocks become available.
 */
@SmellsMessy
public class FileStoreStreamGetMain {

    private final FileSystem fs;
    private final OutputStream out;
    private final WaitHandler waitHandler;

    public FileStoreStreamGetMain(FileSystem fs, OutputStream out,
	    WaitHandler waitHandler) {
	this.fs = fs;
	this.out = out;
	this.waitHandler = waitHandler;
    }

    /**
     * @param args
     *            file store base, request list, key to fetch
     * @throws IOException
     */
    @EntryPoint
    public static void main(String[] args) throws IOException {
	WaitHandler waitHandler = new WaitHandler() {
	    int delay = 1;
	    int lastDelay = 0;

	    @Override
	    public void doWait(boolean progress) throws IOException {
		try {
		if (progress) {
		    delay = 1;
		    lastDelay = 0;

		    Thread.sleep(delay * 1000);
		    return;
		}

		Thread.sleep(delay * 1000);

		int nextDelay = delay + lastDelay;
		lastDelay = delay;
		delay = nextDelay;
		} catch (InterruptedException e) {
		    throw new InterruptedIOException();
		}
	    }
	};

	FileSystem fs = new RealFileSystem();

	OutputStream out = System.out;

	new FileStoreStreamGetMain(fs, out, waitHandler).run(args);
    }

    public void run(String... args) throws IOException {
	FileBlockStore store = new FileBlockStore(fs, args[0]);
	pendingFile = args[1];
	
	KeyInputStream in = new KeyInputStream(new KeyParse()
		.parseFullKey(args[2]), new RetrieveBlockStorage(store),
		notReadyHandler);

	Streams.copy(in, out);
    }

    private String pendingFile;

    private NotReadyHandler notReadyHandler = new NotReadyHandler() {
	int progress;

	@Override
	public void notReady(SplitReader reader) throws IOException {
	    StringBuilder p = new StringBuilder();
	    for (FullKey k : reader.getPending()) {
		p.append(KeyUtil.toLocatorKey(k) + "\n");
	    }
	    fs.save(pendingFile + ".tmp", ByteString.toUtf8(p.toString()));
	    fs.rename(pendingFile + ".tmp", pendingFile);

	    waitHandler.doWait(progress != reader.getProgress());
	    progress = reader.getProgress();
	}
    };
}
