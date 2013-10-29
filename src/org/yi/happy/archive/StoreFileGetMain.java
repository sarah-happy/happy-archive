package org.yi.happy.archive;

import java.io.IOException;
import java.util.List;

import org.yi.happy.annotate.RestoreLoop;
import org.yi.happy.archive.block.Block;
import org.yi.happy.archive.commandLine.UsesArgs;
import org.yi.happy.archive.commandLine.UsesNeed;
import org.yi.happy.archive.commandLine.UsesStore;
import org.yi.happy.archive.key.FullKey;
import org.yi.happy.archive.key.FullKeyParse;
import org.yi.happy.archive.restore.RestoreEngine;

/**
 * get a file from a file store.
 */
@UsesStore
@UsesNeed
@UsesArgs({ "key", "output" })
public class StoreFileGetMain implements MainCommand {
    private final List<String> args;
    private ClearBlockSource source;
    private FragmentSave target;
    private final NotReadyNeedAndWait notReady;

    /**
     * create.
     * 
     * @param source
     *            the block source.
     * @param target
     *            the fragment target.
     * @param waitHandler
     *            what to do when it is time to wait for data.
     * @param needHandler
     *            where to post the needed keys.
     * @param args
     *            the non-option command line arguments.
     */
    public StoreFileGetMain(ClearBlockSource source, FragmentSave target,
            WaitHandler waitHandler, NeedHandler needHandler, List<String> args) {
        this.source = source;
        this.target = target;
        this.notReady = new NotReadyNeedAndWait(needHandler, waitHandler);

        this.args = args;
    }

    /**
     * get a file from a file store.
     * 
     * @param env
     *            the file store, where to write the pending list, the key to
     *            fetch, the output file name.
     * @throws IOException
     */
    @Override
    @RestoreLoop
    public void run() throws IOException {
        FullKey key = FullKeyParse.parseFullKey(args.get(0));
        String path = args.get(1);

        RestoreEngine engine = new RestoreEngine(key);

        try {
            /*
             * do the work
             */
            while (true) {
                boolean progress = false;
                engine.start();
                while (engine.findReady()) {
                    Block block = source.get(engine.getKey());
                    if (block == null) {
                        engine.skip();
                        continue;
                    }

                    Fragment part = engine.step(block);
                    progress = true;

                    if (part != null) {
                        target.save(path, part);
                    }
                }

                if (engine.isDone()) {
                    break;
                }

                target.close();
                notReady.onNotReady(engine, progress);
            }
        } finally {
            target.close();
        }
    }
}
