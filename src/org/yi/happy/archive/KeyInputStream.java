package org.yi.happy.archive;

import java.io.IOException;
import java.io.InputStream;

import org.yi.happy.archive.SplitReader.GetResult;
import org.yi.happy.archive.key.FullKey;

/**
 * an input stream for reading keys from a store.
 * 
 * @author sarah dot a dot happy at gmail dot com
 * 
 */
public class KeyInputStream extends InputStream {

    /**
     * the split reader that does most of the work
     */
    private SplitReader reader;

    /**
     * how much of the stream has been read
     */
    private long offset;

    /**
     * the current data buffer
     */
    private byte[] buff;

    /**
     * where in the stream the buffer starts
     */
    private long buffStart;

    /**
     * notified when there is no block ready to read, to defer the decision of
     * what to do to the client of this class.
     */
    private final NotReadyHandler notReadyHandler;

    /**
     * configure for reading
     * 
     * @param fullKey
     *            the key to read
     * @param store
     *            the store to get blocks from
     * @param notReadyHandler
     *            notified when a block is needed and not found in the store
     */
    public KeyInputStream(FullKey fullKey, RetrieveBlock store,
            NotReadyHandler notReadyHandler) {
        this.notReadyHandler = notReadyHandler;
        this.reader = new SplitReader(fullKey, store);
        this.offset = 0;
    }

    /**
     * read a byte from the stream. in the case of overlap the overlapping area
     * is skipped.
     */
    @Override
    public int read() throws IOException {
        while (true) {
            /*
             * if the buffer is before the current offset, drop it
             */
            if (buff != null && buffStart + buff.length <= offset) {
                buff = null;
            }

            if (buff != null) {
                if (buffStart > offset) {
                    return 0;
                }

                int out = buff[(int) (offset - buffStart)] & 0xff;
                offset++;

                return out;
            }

            if (reader.isDone()) {
                return -1;
            }

            if (reader.getOffset() > offset) {
                offset++;
                return 0;
            }

	    GetResult got;
	    try {
		got = reader.getFirst();
	    } catch (IOException e) {
		got = null;
	    }
            if (got != null) {
		buffStart = got.getOffset();
		buff = got.getData();
            } else {
                notReadyHandler.notReady(reader);
            }
        }
    }
}
