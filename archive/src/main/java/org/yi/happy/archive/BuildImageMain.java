package org.yi.happy.archive;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.yi.happy.annotate.MagicLiteral;
import org.yi.happy.annotate.SmellsMessy;
import org.yi.happy.archive.block.EncodedBlock;
import org.yi.happy.archive.commandLine.UsesArgs;
import org.yi.happy.archive.commandLine.UsesBlockStore;
import org.yi.happy.archive.commandLine.UsesInput;
import org.yi.happy.archive.commandLine.UsesOutput;
import org.yi.happy.archive.key.LocatorKey;
import org.yi.happy.archive.key.LocatorKeyParse;

/**
 * Build an image of a backup disk. The set of files that will be burned to a
 * backup disk.
 */
@UsesBlockStore
@UsesArgs({ "image-path", "size-mb" })
@UsesInput("key-list")
@UsesOutput("size")
public class BuildImageMain implements MainCommand {

    private final FileStore files;
    private final InputStream in;
    private final PrintStream out;
    private final PrintStream err;
    private final BlockStore blocks;
    private final List<String> args;

    /**
     * create with context.
     * 
     * @param blocks
     *            the block store to use.
     * @param files
     *            the file store to use.
     * @param in
     *            the input stream.
     * @param out
     *            the output stream.
     * @param err
     *            the error stream.
     * @param args
     *            the parameters.
     */
    public BuildImageMain(BlockStore blocks, FileStore files, InputStream in,
            PrintStream out, PrintStream err, List<String> args) {
        this.blocks = blocks;
        this.files = files;
        this.in = in;
        this.out = out;
        this.err = err;
        this.args = args;
    }

    /**
     * Build the image.
     * 
     * @param args
     *            the command line arguments.
     * @throws IOException
     */
    @Override
    @SmellsMessy
    @MagicLiteral
    public void run() throws IOException {
        String imagePath = args.get(0);
        int sizeMb = Integer.parseInt(args.get(1));
        IsoEstimate size = new IsoEstimate();
        int count = 0;

        for (String line : new LineIterator(in)) {
            LocatorKey key = LocatorKeyParse.parseLocatorKey(line);
            EncodedBlock block;
            try {
                block = blocks.get(key);
            } catch (DecodeException e) {
                err.println("error loading block: " + key);
                e.printStackTrace(err);
                continue;
            }
            byte[] data = block.asBytes();
            size.add(data.length);
            if (size.getMegaSize() > sizeMb) {
                size.remove(data.length);
                break;
            }
            files.put(imagePath + "/" + String.format("%08x.dat", count), data);
            count++;
        }

        /*
         * Full is 99% of the target capacity.
         */
        String full = "";
        if (size.getMegaSize() > sizeMb * 99 / 100) {
            full = "\tfull";
        }

        out.println(size.getCount() + "\t" + size.getMegaSize() + full);
    }
}
