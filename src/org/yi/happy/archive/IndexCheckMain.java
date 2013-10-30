package org.yi.happy.archive;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.yi.happy.annotate.DuplicatedLogic;
import org.yi.happy.archive.block.EncodedBlock;
import org.yi.happy.archive.block.parser.EncodedBlockParse;
import org.yi.happy.archive.commandLine.UsesArgs;
import org.yi.happy.archive.commandLine.UsesInput;
import org.yi.happy.archive.commandLine.UsesOutput;
import org.yi.happy.archive.crypto.DigestFactory;
import org.yi.happy.archive.crypto.DigestProvider;
import org.yi.happy.archive.crypto.Digests;
import org.yi.happy.archive.file_system.FileSystem;

/**
 * Check that the files on a volume are still readable. Do this by building a
 * new index based on the file names of an existing index.
 */
@UsesArgs({ "image-path" })
@UsesInput("index")
@UsesOutput("index")
public class IndexCheckMain implements MainCommand {
    private final FileSystem fs;
    private final InputStream in;
    private final PrintStream out;
    private final PrintStream err;
    private final List<String> args;

    /**
     * setup the command.
     * 
     * @param fs
     *            the file system to use.
     * @param in
     *            the input stream to use.
     * @param out
     *            the output stream to use.
     * @param err
     *            the error stream to use.
     * @param args
     *            the arguments to use.
     */
    public IndexCheckMain(FileSystem fs, InputStream in, PrintStream out,
            PrintStream err, List<String> args) {
        this.fs = fs;
        this.in = in;
        this.out = out;
        this.err = err;
        this.args = args;
    }

    @Override
    @DuplicatedLogic("with IndexVolumeMain.process")
    public void run() throws Exception {
        String imagePath = args.get(0);

        DigestProvider digest = DigestFactory.getProvider("sha-256");

        LineCursor line = new LineCursor(in);
        while (line.next()) {
            try {
                String name = line.get().split("\t")[0];
                String path = fs.join(imagePath, name);

                byte[] data = fs.load(path, Blocks.MAX_SIZE);
                EncodedBlock block = EncodedBlockParse.parse(data);

                String key = block.getKey().toString();

                data = block.asBytes();

                String hash = Base16.encode(Digests.digestData(digest, data));
                String size = Integer.toString(data.length);

                out.println(name + "\t" + "plain" + "\t" + key + "\t" + hash
                        + "\t" + size);
            } catch (Exception e) {
                e.printStackTrace(err);
            }
        }
    }

}